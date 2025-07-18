import { Injectable, ComponentRef, ViewContainerRef, Type, Injector } from '@angular/core';
import { UiComponent } from '../models/ui-component.model';
import { ComponentRegistry } from './component-registry.service';
import { ComponentService } from './component.service';
import { BaseComponent } from '../../shared/components/dynamic-components/base.component';
import { Observable, forkJoin, of, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ComponentLoaderService {
  constructor(
    private componentRegistry: ComponentRegistry,
    private componentService: ComponentService
  ) {}

  /**
   * Load a single component by its ID and render it to a container
   */
  loadComponentById(
    componentId: string,
    container: ViewContainerRef,
    data?: any
  ): Observable<ComponentRef<BaseComponent> | null> {
    return this.componentService.getComponentById(componentId).pipe(
      map(component => {
        if (!component || !component.id) {
          console.error(`Component with ID ${componentId} not found`);
          return null;
        }
        return this.renderComponent(component, container, data);
      })
    );
  }

  /**
   * Load multiple components by their IDs and render them to a container
   */
  loadComponentsByIds(
    componentIds: string[],
    container: ViewContainerRef,
    data?: any
  ): Observable<(ComponentRef<BaseComponent> | null)[]> {
    if (!componentIds || componentIds.length === 0) {
      return of([]);
    }

    const componentObservables = componentIds.map(id => this.componentService.getComponentById(id));
    
    return forkJoin(componentObservables).pipe(
      map(components => {
        container.clear();
        return components
          .filter(component => component && component.id && component.active)
          .sort((a, b) => a.orderIndex - b.orderIndex)
          .map(component => this.renderComponent(component, container, data));
      })
    );
  }

  /**
   * Render a component to a container
   */
  private renderComponent(
    componentData: UiComponent,
    container: ViewContainerRef,
    data?: any
  ): ComponentRef<BaseComponent> | null {
    if (!componentData || !componentData.componentType) {
      console.error('Invalid component data', componentData);
      return null;
    }

    const componentType = this.componentRegistry.getComponent(componentData.componentType);
    
    if (!componentType) {
      console.error(`Component type ${componentData.componentType} not registered`);
      return null;
    }

    try {
      const componentRef = container.createComponent<BaseComponent>(componentType as Type<BaseComponent>);
      componentRef.instance.component = componentData;
      componentRef.instance.data = data;
      
      // Force change detection
      componentRef.changeDetectorRef.detectChanges();
      
      return componentRef;
    } catch (error) {
      console.error(`Error rendering component ${componentData.name}:`, error);
      return null;
    }
  }
}
