import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ThemeService } from '../../core/services/theme.service';
import { ComponentService } from '../../core/services/component.service';
import { LayoutService } from '../../core/services/layout.service';
import { UiTheme } from '../../core/models/ui-theme.model';
import { UiComponent } from '../../core/models/ui-component.model';
import { UiLayout } from '../../core/models/ui-layout.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  selectedSection: string | null = null;
  themes: UiTheme[] = [];
  components: UiComponent[] = [];
  layouts: UiLayout[] = [];
  
  themeForm: FormGroup;
  componentForm: FormGroup;
  layoutForm: FormGroup;
  
  editingTheme: UiTheme | null = null;
  editingComponent: UiComponent | null = null;
  editingLayout: UiLayout | null = null;
  
  constructor(
    private themeService: ThemeService,
    private componentService: ComponentService,
    private layoutService: LayoutService,
    private fb: FormBuilder
  ) {
    this.themeForm = this.createThemeForm();
    this.componentForm = this.createComponentForm();
    this.layoutForm = this.createLayoutForm();
  }

  ngOnInit(): void {
    this.loadThemes();
    this.loadComponents();
    this.loadLayouts();
  }
  
  selectSection(section: string): void {
    this.selectedSection = section;
  }
  
  loadThemes(): void {
    this.themeService.getAllThemes().subscribe(data => {
      this.themes = data;
    });
  }
  
  loadComponents(): void {
    this.componentService.getAllComponents().subscribe(data => {
      this.components = data;
    });
  }
  
  loadLayouts(): void {
    this.layoutService.getAllLayouts().subscribe(data => {
      this.layouts = data;
    });
  }
  
  createThemeForm(): FormGroup {
    return this.fb.group({
      id: [''],
      name: ['', [Validators.required]],
      displayName: ['', [Validators.required]],
      primaryColor: ['#007bff', [Validators.required]],
      secondaryColor: ['#6c757d', [Validators.required]],
      backgroundColor: ['#ffffff', [Validators.required]],
      textColor: ['#212529', [Validators.required]],
      linkColor: ['#0275d8', [Validators.required]],
      headerBackgroundColor: ['#343a40', [Validators.required]],
      headerTextColor: ['#ffffff', [Validators.required]],
      footerBackgroundColor: ['#343a40', [Validators.required]],
      footerTextColor: ['#ffffff', [Validators.required]],
      accentColor: ['#17a2b8'],
      fontFamily: ['Arial, sans-serif'],
      active: [true]
    });
  }
  
  createComponentForm(): FormGroup {
    return this.fb.group({
      id: [''],
      name: ['', [Validators.required]],
      displayName: ['', [Validators.required]],
      type: ['', [Validators.required]],
      properties: this.fb.group({}),
      style: this.fb.group({}),
      content: [''],
      active: [true]
    });
  }
  
  createLayoutForm(): FormGroup {
    return this.fb.group({
      id: [''],
      name: ['', [Validators.required]],
      displayName: ['', [Validators.required]],
      route: ['', [Validators.required]],
      components: [[]],
      theme: ['', [Validators.required]],
      sections: this.fb.array([]),
      style: this.fb.group({}),
      active: [true]
    });
  }
  
  createNewTheme(): void {
    console.log('Create new theme clicked');
    this.editingTheme = null;
    this.themeForm.reset();
    this.themeForm.patchValue({
      primaryColor: '#007bff',
      secondaryColor: '#6c757d',
      backgroundColor: '#ffffff',
      textColor: '#212529',
      linkColor: '#0275d8',
      headerBackgroundColor: '#343a40',
      headerTextColor: '#ffffff',
      footerBackgroundColor: '#343a40',
      footerTextColor: '#ffffff',
      accentColor: '#17a2b8',
      fontFamily: 'Arial, sans-serif',
      active: true
    });
  }
  
  editTheme(theme: UiTheme): void {
    console.log('Edit theme clicked', theme);
    this.editingTheme = theme;
    this.themeForm.patchValue(theme);
  }
  
  deleteTheme(theme: UiTheme): void {
    console.log('Delete theme clicked', theme);
    // We'll just use local data for now
    this.themes = this.themes.filter(t => t.id !== theme.id);
  }
  
  saveTheme(): void {
    if (this.themeForm.invalid) {
      // Mark all fields as touched to show validation errors
      Object.keys(this.themeForm.controls).forEach(key => {
        const control = this.themeForm.get(key);
        control?.markAsTouched();
      });
      console.log('Form is invalid', this.themeForm.errors, this.themeForm.value);
      return;
    }
    
    const themeData = this.themeForm.value;
    console.log('Saving theme', themeData);
    
    if (this.editingTheme) {
      // Update existing theme
      const index = this.themes.findIndex(t => t.id === themeData.id);
      if (index >= 0) {
        this.themes[index] = themeData;
      }
      this.editingTheme = null;
    } else {
      // Create new theme
      themeData.id = 'theme_' + new Date().getTime();
      this.themes.push(themeData);
    }
    
    this.themeForm.reset();
  }
  
  createNewComponent(): void {
    console.log('Create new component clicked');
    this.editingComponent = null;
    this.componentForm.reset();
    this.componentForm.patchValue({
      active: true
    });
  }
  
  editComponent(component: UiComponent): void {
    console.log('Edit component clicked', component);
    this.editingComponent = component;
    this.componentForm.patchValue(component);
  }
  
  deleteComponent(component: UiComponent): void {
    console.log('Delete component clicked', component);
    this.components = this.components.filter(c => c.id !== component.id);
  }
  
  saveComponent(): void {
    if (this.componentForm.invalid) {
      return;
    }
    
    const componentData = this.componentForm.value;
    console.log('Saving component', componentData);
    
    if (this.editingComponent) {
      const index = this.components.findIndex(c => c.id === componentData.id);
      if (index >= 0) {
        this.components[index] = componentData;
      }
      this.editingComponent = null;
    } else {
      componentData.id = 'component_' + new Date().getTime();
      this.components.push(componentData);
    }
    
    this.componentForm.reset();
  }
  
  createNewLayout(): void {
    console.log('Create new layout clicked');
    this.editingLayout = null;
    this.layoutForm.reset();
    this.layoutForm.patchValue({
      active: true,
      components: []
    });
  }
  
  editLayout(layout: UiLayout): void {
    console.log('Edit layout clicked', layout);
    this.editingLayout = layout;
    this.layoutForm.patchValue(layout);
  }
  
  deleteLayout(layout: UiLayout): void {
    console.log('Delete layout clicked', layout);
    this.layouts = this.layouts.filter(l => l.id !== layout.id);
  }
  
  saveLayout(): void {
    if (this.layoutForm.invalid) {
      return;
    }
    
    const layoutData = this.layoutForm.value;
    console.log('Saving layout', layoutData);
    
    if (this.editingLayout) {
      const index = this.layouts.findIndex(l => l.id === layoutData.id);
      if (index >= 0) {
        this.layouts[index] = layoutData;
      }
      this.editingLayout = null;
    } else {
      layoutData.id = 'layout_' + new Date().getTime();
      this.layouts.push(layoutData);
    }
    
    this.layoutForm.reset();
  }
  
  cancelEdit(): void {
    this.editingTheme = null;
    this.editingComponent = null;
    this.editingLayout = null;
    this.themeForm.reset();
    this.componentForm.reset();
    this.layoutForm.reset();
  }
  
  applyTheme(theme: UiTheme): void {
    console.log('Apply theme clicked', theme);
    this.themeService.setCurrentTheme(theme);
  }
  
  logFormState(): void {
    console.log('Form value:', this.themeForm.value);
    console.log('Form valid:', this.themeForm.valid);
    console.log('Form touched:', this.themeForm.touched);
    console.log('Form dirty:', this.themeForm.dirty);
    
    // Log each control's state
    Object.keys(this.themeForm.controls).forEach(key => {
      const control = this.themeForm.get(key);
      console.log(`${key}:`, {
        value: control?.value,
        valid: control?.valid,
        touched: control?.touched,
        errors: control?.errors
      });
      
      // Mark as touched to show validation errors
      control?.markAsTouched();
    });
  }
}
