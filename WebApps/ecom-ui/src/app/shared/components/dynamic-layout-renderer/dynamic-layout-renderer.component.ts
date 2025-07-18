import { Component, OnInit, ViewContainerRef, ViewChild, Input, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutService } from '../../../core/services/layout.service';
import { ComponentLoaderService } from '../../../core/services/component-loader.service';
import { ThemeService } from '../../../core/services/theme.service';
import { UiLayout } from '../../../core/models/ui-layout.model';
import { switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-dynamic-layout-renderer',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dynamic-layout" *ngIf="layout" [ngStyle]="layoutStyles">
      <ng-container *ngIf="!layout.sections || layout.sections.length === 0">
        <div class="component-container">
          <ng-container #componentContainer></ng-container>
        </div>
      </ng-container>
      
      <ng-container *ngIf="layout.sections && layout.sections.length > 0">
        <div *ngFor="let section of layout.sections" class="layout-section" 
             [ngStyle]="getSectionStyles(section)">
          <div class="component-container" [ngClass]="getSectionClass(section)">
            <ng-container #sectionContainer></ng-container>
          </div>
        </div>
      </ng-container>
    </div>
  `,
  styles: [`
    .dynamic-layout {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }
    
    .layout-section {
      width: 100%;
    }
    
    .component-container {
      width: 100%;
    }
    
    .container-fluid {
      padding-left: 0;
      padding-right: 0;
    }
  `]
})
export class DynamicLayoutRendererComponent implements OnInit, AfterViewInit {
  @Input() routePath: string = '/';
  @Input() layoutId: string = '';
  @Input() layoutName: string = '';
  
  @ViewChild('componentContainer', { read: ViewContainerRef, static: false }) 
  componentContainer!: ViewContainerRef;
  
  @ViewChild('sectionContainer', { read: ViewContainerRef, static: false, }) 
  sectionContainers!: ViewContainerRef;
  
  layout!: UiLayout;
  layoutStyles: { [key: string]: string } = {};
  
  constructor(
    private layoutService: LayoutService,
    private componentLoader: ComponentLoaderService,
    private themeService: ThemeService
  ) {}
  
  ngOnInit(): void {
    this.loadLayout();
  }
  
  ngAfterViewInit(): void {
    // Components will be loaded after the layout is loaded
  }
  
  loadLayout(): void {
    let layoutObservable;
    
    if (this.layoutId) {
      layoutObservable = this.layoutService.getLayoutById(this.layoutId);
    } else if (this.layoutName) {
      layoutObservable = this.layoutService.getLayoutByName(this.layoutName);
    } else {
      layoutObservable = this.layoutService.getLayoutByRoute(this.routePath);
    }
    
    layoutObservable.pipe(
      tap(layout => {
        if (!layout || !layout.id) {
          console.error('Layout not found');
          return;
        }
        
        this.layout = layout as UiLayout;
        this.applyLayoutStyles();
        
        // Set theme if specified
        if (layout.theme) {
          this.themeService.setThemeById(layout.theme);
        }
      }),
      switchMap(layout => {
        if (!layout || !layout.id) {
          return of(null);
        }
        
        // Delay loading components until after view init
        setTimeout(() => {
          this.loadComponents();
        }, 0);
        
        return of(layout);
      })
    ).subscribe();
  }
  
  applyLayoutStyles(): void {
    if (this.layout.style) {
      this.layoutStyles = {};
      Object.keys(this.layout.style).forEach(key => {
        this.layoutStyles[key] = String(this.layout.style![key]);
      });
    }
  }
  
  getSectionStyles(section: any): { [key: string]: string } {
    if (!section.style) {
      return {};
    }
    
    const styles: { [key: string]: string } = {};
    Object.keys(section.style).forEach(key => {
      styles[key] = String(section.style[key]);
    });
    
    return styles;
  }
  
  getSectionClass(section: any): string {
    return section.properties?.fullWidth ? 'container-fluid' : 'container';
  }
  
  loadComponents(): void {
    if (!this.layout) {
      return;
    }
    
    if (this.layout.sections && this.layout.sections.length > 0 && this.sectionContainers) {
      // Load components into sections
      this.layout.sections.forEach(section => {
        if (section.componentIds && section.componentIds.length > 0) {
          const sectionData = section.properties || {};
          this.componentLoader.loadComponentsByIds(
            section.componentIds, 
            this.sectionContainers,
            sectionData
          ).subscribe();
        }
      });
    } else if (this.layout.components && this.layout.components.length > 0 && this.componentContainer) {
      // Load components directly into the main container
      this.componentLoader.loadComponentsByIds(
        this.layout.components, 
        this.componentContainer
      ).subscribe();
    }
  }
}
