import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { LayoutService } from '../../../../core/services/layout.service';
import { ComponentService } from '../../../../core/services/component.service';
import { ThemeService } from '../../../../core/services/theme.service';
import { UiLayout } from '../../../../core/models/ui-layout.model';
import { UiComponent } from '../../../../core/models/ui-component.model';
import { UiTheme } from '../../../../core/models/ui-theme.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-layout-editor',
  templateUrl: './layout-editor.component.html',
  styleUrls: ['./layout-editor.component.css']
})
export class LayoutEditorComponent implements OnInit {
  layouts: UiLayout[] = [];
  components: UiComponent[] = [];
  themes: UiTheme[] = [];
  layoutForm: FormGroup;
  selectedLayout: UiLayout | null = null;
  isEditing: boolean = false;
  isCreating: boolean = false;
  activeTab: 'components' | 'sections' | 'style' = 'components';
  
  private baseUrl = `${environment.apiUrl}/ui-service/api/ui/layouts`;
  
  constructor(
    private layoutService: LayoutService,
    private componentService: ComponentService,
    private themeService: ThemeService,
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) {
    this.layoutForm = this.createLayoutForm();
  }

  ngOnInit(): void {
    this.loadLayouts();
    this.loadComponents();
    this.loadThemes();
  }

  createLayoutForm(): FormGroup {
    return this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required, Validators.minLength(3)]],
      displayName: ['', [Validators.required]],
      components: [[] as string[]],
      route: ['', [Validators.required]],
      theme: ['', [Validators.required]],
      active: [true],
      sections: this.formBuilder.array([]) as FormArray,
      style: this.formBuilder.group({})
    });
  }

  loadLayouts(): void {
    this.layoutService.getAllLayouts().subscribe(layouts => {
      this.layouts = layouts;
    });
  }

  loadComponents(): void {
    this.componentService.getAllComponents().subscribe(components => {
      this.components = components.filter(c => c.active);
    });
  }

  loadThemes(): void {
    this.themeService.getAllThemes().subscribe(themes => {
      this.themes = themes;
    });
  }

  get sectionsArray(): FormArray {
    return this.layoutForm.get('sections') as FormArray;
  }

  createSectionGroup(): FormGroup {
    return this.formBuilder.group({
      id: [this.generateId()],
      name: ['', Validators.required],
      displayName: ['', Validators.required],
      componentIds: [[] as string[]],
      orderIndex: [0, Validators.required],
      style: this.formBuilder.group({}),
      properties: this.formBuilder.group({
        fullWidth: [false]
      })
    });
  }

  generateId(): string {
    return 'section_' + new Date().getTime().toString();
  }

  editLayout(layout: UiLayout): void {
    this.selectedLayout = layout;
    
    // Reset the form
    this.layoutForm = this.createLayoutForm();
    
    // Create sections form array
    const sectionsArray = this.formBuilder.array([]);
    if (layout.sections && layout.sections.length > 0) {
      layout.sections.forEach(section => {
        const sectionGroup = this.formBuilder.group({
          id: [section.id],
          name: [section.name, Validators.required],
          displayName: [section.displayName, Validators.required],
          componentIds: [section.componentIds || []],
          orderIndex: [section.orderIndex, Validators.required],
          style: this.formBuilder.group({}),
          properties: this.formBuilder.group({
            fullWidth: [section.properties?.fullWidth || false]
          })
        });
        
        if (section.style) {
          const styleGroup = sectionGroup.get('style') as FormGroup;
          Object.keys(section.style).forEach(key => {
            styleGroup.addControl(key, this.formBuilder.control(section.style![key]));
          });
        }
        
        const sectionsArray = this.layoutForm.get('sections') as FormArray;
        sectionsArray.push(sectionGroup);
      });
    }
    
    // Create style form group
    const styleGroup = this.formBuilder.group({});
    if (layout.style) {
      Object.keys(layout.style).forEach(key => {
        styleGroup.addControl(key, this.formBuilder.control(layout.style![key]));
      });
    }
    
    // Set the form value
    this.layoutForm.setControl('sections', sectionsArray);
    this.layoutForm.setControl('style', styleGroup);
    
    this.layoutForm.patchValue({
      id: layout.id,
      name: layout.name,
      displayName: layout.displayName,
      components: layout.components,
      route: layout.route,
      theme: layout.theme,
      active: layout.active
    });
    
    this.isEditing = true;
    this.isCreating = false;
  }

  createNewLayout(): void {
    this.selectedLayout = null;
    this.layoutForm = this.createLayoutForm();
    this.isCreating = true;
    this.isEditing = false;
    this.activeTab = 'components';
  }

  cancelEdit(): void {
    this.selectedLayout = null;
    this.layoutForm = this.createLayoutForm();
    this.isEditing = false;
    this.isCreating = false;
  }

  setActiveTab(tab: 'components' | 'sections' | 'style'): void {
    this.activeTab = tab;
  }

  addSection(): void {
    const sectionGroup = this.createSectionGroup();
    const sectionsArray = this.layoutForm.get('sections') as FormArray;
    sectionsArray.push(sectionGroup);
  }

  removeSection(index: number): void {
    this.sectionsArray.removeAt(index);
  }

  addStyle(): void {
    const styleName = prompt('Enter CSS property name:');
    if (styleName && styleName.trim() !== '') {
      const styleGroup = this.layoutForm.get('style') as FormGroup;
      styleGroup.addControl(styleName, this.formBuilder.control(''));
    }
  }

  removeStyle(styleName: string): void {
    const styleGroup = this.layoutForm.get('style') as FormGroup;
    styleGroup.removeControl(styleName);
  }

  addSectionStyle(sectionIndex: number): void {
    const styleName = prompt('Enter CSS property name:');
    if (styleName && styleName.trim() !== '') {
      const sectionGroup = this.sectionsArray.at(sectionIndex) as FormGroup;
      const styleGroup = sectionGroup.get('style') as FormGroup;
      styleGroup.addControl(styleName, this.formBuilder.control(''));
    }
  }

  removeSectionStyle(sectionIndex: number, styleName: string): void {
    const sectionGroup = this.sectionsArray.at(sectionIndex) as FormGroup;
    const styleGroup = sectionGroup.get('style') as FormGroup;
    styleGroup.removeControl(styleName);
  }

  getStyleControls(): { key: string, control: any }[] {
    const styleGroup = this.layoutForm.get('style') as FormGroup;
    return Object.keys(styleGroup.controls).map(key => {
      return { key, control: styleGroup.get(key) };
    });
  }

  getSectionStyleControls(sectionIndex: number): { key: string, control: any }[] {
    const sectionGroup = this.sectionsArray.at(sectionIndex) as FormGroup;
    const styleGroup = sectionGroup.get('style') as FormGroup;
    return Object.keys(styleGroup.controls).map(key => {
      return { key, control: styleGroup.get(key) };
    });
  }

  onComponentSelectionChange(event: any): void {
    // Handle component selection changes
    const componentIds = this.layoutForm.get('components')?.value || [];
    console.log('Selected components:', componentIds);
  }

  onSectionComponentSelectionChange(sectionIndex: number, event: any): void {
    // Handle section component selection changes
    const sectionGroup = this.sectionsArray.at(sectionIndex) as FormGroup;
    const componentIds = sectionGroup.get('componentIds')?.value || [];
    console.log(`Section ${sectionIndex} selected components:`, componentIds);
  }

  saveLayout(): void {
    if (this.layoutForm.invalid) {
      return;
    }

    const layoutData = this.layoutForm.value;
    
    if (this.isEditing && this.selectedLayout) {
      // Update existing layout
      this.http.put(`${this.baseUrl}/${this.selectedLayout.id}`, layoutData)
        .subscribe(
          () => {
            this.loadLayouts();
            this.cancelEdit();
          },
          error => {
            console.error('Error updating layout:', error);
          }
        );
    } else {
      // Create new layout
      this.http.post(this.baseUrl, layoutData)
        .subscribe(
          () => {
            this.loadLayouts();
            this.cancelEdit();
          },
          error => {
            console.error('Error creating layout:', error);
          }
        );
    }
  }

  deleteLayout(layout: UiLayout): void {
    if (confirm(`Are you sure you want to delete the layout "${layout.displayName}"?`)) {
      this.http.delete(`${this.baseUrl}/${layout.id}`)
        .subscribe(
          () => {
            this.loadLayouts();
            if (this.selectedLayout && this.selectedLayout.id === layout.id) {
              this.cancelEdit();
            }
          },
          error => {
            console.error('Error deleting layout:', error);
          }
        );
    }
  }

  toggleLayoutActive(layout: UiLayout): void {
    const updatedLayout = { ...layout, active: !layout.active };
    this.http.put(`${this.baseUrl}/${layout.id}`, updatedLayout)
      .subscribe(
        () => {
          this.loadLayouts();
        },
        error => {
          console.error('Error updating layout:', error);
        }
      );
  }

  moveUpSection(index: number): void {
    if (index <= 0) return;
    
    // Get the current order values
    const currentSection = this.sectionsArray.at(index) as FormGroup;
    const previousSection = this.sectionsArray.at(index - 1) as FormGroup;
    const currentOrder = currentSection.get('orderIndex')?.value;
    const previousOrder = previousSection.get('orderIndex')?.value;
    
    // Swap order values
    currentSection.get('orderIndex')?.setValue(previousOrder);
    previousSection.get('orderIndex')?.setValue(currentOrder);
    
    // Swap sections in the form array
    moveItemInArray(this.sectionsArray.controls, index, index - 1);
  }

  moveDownSection(index: number): void {
    if (index >= this.sectionsArray.length - 1) return;
    
    // Get the current order values
    const currentSection = this.sectionsArray.at(index) as FormGroup;
    const nextSection = this.sectionsArray.at(index + 1) as FormGroup;
    const currentOrder = currentSection.get('orderIndex')?.value;
    const nextOrder = nextSection.get('orderIndex')?.value;
    
    // Swap order values
    currentSection.get('orderIndex')?.setValue(nextOrder);
    nextSection.get('orderIndex')?.setValue(currentOrder);
    
    // Swap sections in the form array
    moveItemInArray(this.sectionsArray.controls, index, index + 1);
  }

  getComponentById(id: string): UiComponent | undefined {
    return this.components.find(c => c.id === id);
  }

  getThemeById(id: string): UiTheme | undefined {
    return this.themes.find(t => t.id === id);
  }
}
