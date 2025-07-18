import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BootstrapService {
  private renderer: Renderer2;

  constructor(rendererFactory: RendererFactory2) {
    this.renderer = rendererFactory.createRenderer(null, null);
  }

  /**
   * Initialize Bootstrap dropdowns
   */
  initializeDropdowns(): void {
    if (typeof window !== 'undefined') {
      // Use the global function if available, otherwise use our implementation
      if (typeof window['initializeBootstrapComponents'] === 'function') {
        window['initializeBootstrapComponents']();
      } else {
        this.initializeDropdownsDirectly();
      }
    }
  }

  /**
   * Direct implementation of dropdown initialization
   */
  private initializeDropdownsDirectly(): void {
    if (typeof window !== 'undefined' && typeof window['bootstrap'] !== 'undefined') {
      setTimeout(() => {
        const dropdownElementList = document.querySelectorAll('.dropdown-toggle');
        dropdownElementList.forEach(dropdownToggleEl => {
          // Check if dropdown is already initialized
          if (!window['bootstrap'].Dropdown.getInstance(dropdownToggleEl)) {
            new window['bootstrap'].Dropdown(dropdownToggleEl, {
              autoClose: 'outside'
            });
          }
        });
      }, 100);
    }
  }

  /**
   * Add click event listeners to dropdown items that need to close the dropdown when clicked
   */
  addDropdownItemListeners(): void {
    setTimeout(() => {
      const dropdownItems = document.querySelectorAll('.dropdown-item');
      dropdownItems.forEach(item => {
        // Remove existing listeners first to avoid duplicates
        const newItem = item.cloneNode(true);
        if (item.parentNode) {
          item.parentNode.replaceChild(newItem, item);
        }
        
        // Add new listener
        this.renderer.listen(newItem, 'click', () => {
          // Close all dropdowns when an item is clicked
          const openDropdownToggle = document.querySelector('.dropdown-toggle.show');
          if (openDropdownToggle && typeof window['bootstrap'] !== 'undefined') {
            const dropdown = window['bootstrap'].Dropdown.getInstance(openDropdownToggle);
            if (dropdown) {
              dropdown.hide();
            }
          }
        });
      });
    }, 200);
  }

  /**
   * Initialize all Bootstrap components (tooltips, popovers, etc.)
   */
  initializeAllComponents(): void {
    this.initializeDropdowns();
    this.addDropdownItemListeners();
    this.initializeTooltips();
    
    // Dispatch custom event to notify that components have been initialized
    if (typeof document !== 'undefined') {
      document.dispatchEvent(new CustomEvent('bootstrap-components-initialized'));
    }
  }

  /**
   * Initialize Bootstrap tooltips
   */
  private initializeTooltips(): void {
    if (typeof window !== 'undefined' && typeof window['bootstrap'] !== 'undefined') {
      setTimeout(() => {
        const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
        tooltipTriggerList.forEach(tooltipTriggerEl => {
          // Check if tooltip is already initialized
          if (!window['bootstrap'].Tooltip.getInstance(tooltipTriggerEl)) {
            new window['bootstrap'].Tooltip(tooltipTriggerEl);
          }
        });
      }, 100);
    }
  }
}
