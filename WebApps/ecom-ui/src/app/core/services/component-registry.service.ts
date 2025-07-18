import { Injectable, Type } from '@angular/core';
import { DynamicHeaderComponent } from '../../shared/components/dynamic-components/header/dynamic-header.component';
import { DynamicFooterComponent } from '../../shared/components/dynamic-components/footer/dynamic-footer.component';
import { DynamicHeroImageComponent } from '../../shared/components/dynamic-components/hero-image/dynamic-hero-image.component';
import { DynamicCategoryNavComponent } from '../../shared/components/dynamic-components/category-nav/dynamic-category-nav.component';
import { DynamicProductGridComponent } from '../../shared/components/dynamic-components/product-grid/dynamic-product-grid.component';
import { BaseComponent } from '../../shared/components/dynamic-components/base.component';

@Injectable({
  providedIn: 'root'
})
export class ComponentRegistry {
  private registry = new Map<string, Type<BaseComponent>>();

  constructor() {
    this.registerComponents();
  }

  private registerComponents(): void {
    // Register all dynamic components here
    this.register('header', DynamicHeaderComponent);
    this.register('footer', DynamicFooterComponent);
    this.register('hero-image', DynamicHeroImageComponent);
    this.register('category-nav', DynamicCategoryNavComponent);
    this.register('product-grid', DynamicProductGridComponent);
    // Other components will be registered as they are implemented
  }

  register(type: string, component: Type<BaseComponent>): void {
    this.registry.set(type, component);
  }

  getComponent(type: string): Type<BaseComponent> | null {
    return this.registry.get(type) || null;
  }

  isRegistered(type: string): boolean {
    return this.registry.has(type);
  }
}
