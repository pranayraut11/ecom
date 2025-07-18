// This file provides helper functions for Bootstrap components
// It will be loaded after Angular app is initialized

document.addEventListener('DOMContentLoaded', function() {
  console.log('DOM fully loaded, initializing Bootstrap components');
  initializeBootstrapComponents();
  
  // Add direct event listener for profile dropdown toggle
  setTimeout(setupManualDropdowns, 500);
});

// Function to manually set up dropdowns without relying on Bootstrap's data-bs-toggle
function setupManualDropdowns() {
  if (typeof jQuery !== 'undefined') {
    // Profile dropdown toggle
    jQuery(document).on('click', '#profileDropdown', function(e) {
      e.preventDefault();
      e.stopPropagation();
      var $dropdown = jQuery(this).next('.dropdown-menu');
      
      if ($dropdown.hasClass('show')) {
        $dropdown.removeClass('show');
        jQuery(this).attr('aria-expanded', 'false');
      } else {
        // Hide all other dropdowns
        jQuery('.dropdown-menu.show').removeClass('show');
        jQuery('.dropdown-toggle').attr('aria-expanded', 'false');
        
        // Show this dropdown
        $dropdown.addClass('show');
        jQuery(this).attr('aria-expanded', 'true');
      }
    });
    
    // Close dropdowns when clicking outside
    jQuery(document).on('click', function(e) {
      if (!jQuery(e.target).closest('.dropdown').length) {
        jQuery('.dropdown-menu.show').removeClass('show');
        jQuery('.dropdown-toggle').attr('aria-expanded', 'false');
      }
    });
    
    // Prevent dropdown from closing when clicking inside it
    jQuery(document).on('click', '.dropdown-menu', function(e) {
      e.stopPropagation();
    });
    
    console.log('Manual dropdown handlers set up with jQuery');
  } else {
    console.warn('jQuery not found for manual dropdown handling');
  }
}

// Global function to initialize all Bootstrap components
window.initializeBootstrapComponents = function() {
  console.log('Initializing Bootstrap components globally');
  
  // Initialize all dropdowns with proper options
  try {
    var dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    dropdownElementList.forEach(function(dropdownToggleEl) {
      if (!bootstrap.Dropdown.getInstance(dropdownToggleEl)) {
        new bootstrap.Dropdown(dropdownToggleEl, {
          autoClose: 'outside'
        });
      }
    });
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.forEach(function(tooltipTriggerEl) {
      if (!bootstrap.Tooltip.getInstance(tooltipTriggerEl)) {
        new bootstrap.Tooltip(tooltipTriggerEl);
      }
    });
  } catch (e) {
    console.error('Error initializing Bootstrap components:', e);
  }
  
  // Also set up manual handlers as a fallback
  setTimeout(setupManualDropdowns, 200);
};

// Re-initialize when routes change (attach to document for global access)
document.addEventListener('route-changed', function() {
  setTimeout(function() {
    window.initializeBootstrapComponents();
    setupManualDropdowns();
  }, 200);
});

// Function that Angular components can call directly
window.initBootstrapComponents = function() {
  window.initializeBootstrapComponents();
};
