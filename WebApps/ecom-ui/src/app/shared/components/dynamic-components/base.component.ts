import { Input, Directive } from '@angular/core';
import { UiComponent } from '../../../core/models/ui-component.model';

@Directive()
export abstract class BaseComponent {
  @Input() component!: UiComponent;
  @Input() data: any;

  getProperty<T>(key: string, defaultValue: T): T {
    return this.component?.properties && this.component.properties[key] !== undefined 
      ? this.component.properties[key] 
      : defaultValue;
  }

  getStyle(key: string, defaultValue: string): string {
    return this.component?.style && this.component.style[key] !== undefined
      ? String(this.component.style[key])
      : defaultValue;
  }

  getStyleObject(): { [key: string]: string } {
    const styles: { [key: string]: string } = {};
    
    if (this.component?.style) {
      Object.keys(this.component.style).forEach(key => {
        styles[key] = String(this.component.style![key]);
      });
    }
    
    return styles;
  }
}
