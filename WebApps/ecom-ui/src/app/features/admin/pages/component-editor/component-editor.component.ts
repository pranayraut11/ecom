import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { ComponentService } from '../../../../core/services/component.service';
import { UiComponent } from '../../../../core/models/ui-component.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-component-editor',
  templateUrl: './component-editor.component.html',
  styleUrls: ['./component-editor.component.css']
})
export class ComponentEditorComponent implements OnInit {
  components: UiComponent[] = [];
  componentForm: FormGroup;
  selectedComponent: UiComponent | null = null;
  componentTypes: string[] = ['header', 'footer', 'hero-image', 'product-grid', 'category-nav'];
  isEditing: boolean = false;
  isCreating: boolean = false;
  activeTab: 'properties' | 'style' = 'properties';
  
  private baseUrl = `${environment.apiUrl}/ui-service/api/ui/components`;
  
  constructor(
    private componentService: ComponentService,
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) {
    this.componentForm = this.createComponentForm();
  }

  ngOnInit(): void {
    this.loadComponents();
  }

  createComponentForm(): FormGroup {
    return this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required, Validators.minLength(3)]],
      displayName: ['', [Validators.required]],
      componentType: ['', [Validators.required]],
      properties: this.formBuilder.group({}),
      style: this.formBuilder.group({}),
      orderIndex: [0, [Validators.required, Validators.min(0)]],
      active: [true]
    });
  }

  loadComponents(): void {
    this.componentService.getAllComponents().subscribe(components => {
      this.components = components.sort((a, b) => a.orderIndex - b.orderIndex);
    });
  }

  editComponent(component: UiComponent): void {
    this.selectedComponent = component;
    
    // Reset the form first
    this.componentForm = this.createComponentForm();
    
    // Then rebuild property and style form groups based on the selected component
    const propertiesGroup = this.formBuilder.group({});
    if (component.properties) {
      Object.keys(component.properties).forEach(key => {
        propertiesGroup.addControl(key, this.formBuilder.control(component.properties[key]));
      });
    }
    
    const styleGroup = this.formBuilder.group({});
    if (component.style) {
      Object.keys(component.style).forEach(key => {
        styleGroup.addControl(key, this.formBuilder.control(component.style[key]));
      });
    }
    
    // Now patch the main form
    this.componentForm.setControl('properties', propertiesGroup);
    this.componentForm.setControl('style', styleGroup);
    
    this.componentForm.patchValue({
      id: component.id,
      name: component.name,
      displayName: component.displayName,
      componentType: component.componentType,
      orderIndex: component.orderIndex,
      active: component.active
    });
    
    this.isEditing = true;
    this.isCreating = false;
  }

  createNewComponent(): void {
    this.selectedComponent = null;
    this.componentForm = this.createComponentForm();
    this.isCreating = true;
    this.isEditing = false;
    this.activeTab = 'properties';
  }

  cancelEdit(): void {
    this.selectedComponent = null;
    this.componentForm = this.createComponentForm();
    this.isEditing = false;
    this.isCreating = false;
  }

  setActiveTab(tab: 'properties' | 'style'): void {
    this.activeTab = tab;
  }

  addProperty(): void {
    const propertyName = prompt('Enter property name:');
    if (propertyName && propertyName.trim() !== '') {
      const propertiesGroup = this.componentForm.get('properties') as FormGroup;
      propertiesGroup.addControl(propertyName, this.formBuilder.control(''));
    }
  }

  addStyle(): void {
    const styleName = prompt('Enter CSS property name:');
    if (styleName && styleName.trim() !== '') {
      const styleGroup = this.componentForm.get('style') as FormGroup;
      styleGroup.addControl(styleName, this.formBuilder.control(''));
    }
  }

  removeProperty(propertyName: string): void {
    const propertiesGroup = this.componentForm.get('properties') as FormGroup;
    propertiesGroup.removeControl(propertyName);
  }

  removeStyle(styleName: string): void {
    const styleGroup = this.componentForm.get('style') as FormGroup;
    styleGroup.removeControl(styleName);
  }

  getPropertyControls(): { key: string, control: any }[] {
    const propertiesGroup = this.componentForm.get('properties') as FormGroup;
    return Object.keys(propertiesGroup.controls).map(key => {
      return { key, control: propertiesGroup.get(key) };
    });
  }

  getStyleControls(): { key: string, control: any }[] {
    const styleGroup = this.componentForm.get('style') as FormGroup;
    return Object.keys(styleGroup.controls).map(key => {
      return { key, control: styleGroup.get(key) };
    });
  }

  saveComponent(): void {
    if (this.componentForm.invalid) {
      return;
    }

    const componentData = this.componentForm.value;
    
    if (this.isEditing && this.selectedComponent) {
      // Update existing component
      this.http.put(`${this.baseUrl}/${this.selectedComponent.id}`, componentData)
        .subscribe(
          () => {
            this.loadComponents();
            this.cancelEdit();
          },
          error => {
            console.error('Error updating component:', error);
          }
        );
    } else {
      // Create new component
      this.http.post(this.baseUrl, componentData)
        .subscribe(
          () => {
            this.loadComponents();
            this.cancelEdit();
          },
          error => {
            console.error('Error creating component:', error);
          }
        );
    }
  }

  deleteComponent(component: UiComponent): void {
    if (confirm(`Are you sure you want to delete the component "${component.displayName}"?`)) {
      this.http.delete(`${this.baseUrl}/${component.id}`)
        .subscribe(
          () => {
            this.loadComponents();
            if (this.selectedComponent && this.selectedComponent.id === component.id) {
              this.cancelEdit();
            }
          },
          error => {
            console.error('Error deleting component:', error);
          }
        );
    }
  }

  toggleComponentActive(component: UiComponent): void {
    const updatedComponent = { ...component, active: !component.active };
    this.http.put(`${this.baseUrl}/${component.id}`, updatedComponent)
      .subscribe(
        () => {
          this.loadComponents();
        },
        error => {
          console.error('Error updating component:', error);
        }
      );
  }

  getDefaultPropertiesForType(type: string): { [key: string]: any } {
    switch(type) {
      case 'header':
        return {
          showLogo: true,
          logoSrc: '/assets/images/logo.png',
          logoAlt: 'Store Logo',
          showSearch: true,
          showAccount: true,
          showCart: true
        };
      case 'footer':
        return {
          showCategories: true,
          showContactInfo: true,
          showSocialLinks: true,
          copyrightText: '© 2025 EcommStore. All rights reserved.',
          showNewsletter: true
        };
      case 'hero-image':
        return {
          title: 'Welcome to Our Store',
          subtitle: 'Discover amazing products at great prices',
          imageSrc: '/assets/images/hero-banner.jpg',
          ctaText: 'Shop Now',
          ctaLink: '/products',
          contentPosition: 'center'
        };
      case 'product-grid':
        return {
          showAddToCart: true,
          columns: 4,
          itemsPerPage: 12,
          showPrices: true,
          showWishlist: true,
          showRatings: true
        };
      case 'category-nav':
        return {
          showIcons: true,
          showAll: true,
          maxCategories: 8
        };
      default:
        return {};
    }
  }

  onComponentTypeChange(event: Event): void {
    const type = (event.target as HTMLSelectElement).value;
    const defaultProps = this.getDefaultPropertiesForType(type);
    
    // Reset the properties group
    const propertiesGroup = this.formBuilder.group({});
    
    // Add default properties for the selected type
    Object.keys(defaultProps).forEach(key => {
      propertiesGroup.addControl(key, this.formBuilder.control(defaultProps[key]));
    });
    
    // Set the new properties group on the form
    this.componentForm.setControl('properties', propertiesGroup);
  }
}
