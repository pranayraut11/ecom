import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-tenant-create',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule],
  templateUrl: './tenant-create.component.html',
  styleUrls: ['./tenant-create.component.scss']
})
export class TenantCreateComponent {
  tenantForm: FormGroup;
  isSubmitted = false;
  showSuccess = false;
  generatedTenantId = '';

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.generatedTenantId = 'TEN-' + Math.random().toString(36).substring(2, 10).toUpperCase();

    this.tenantForm = this.fb.group({
      tenantName: ['', [Validators.required, Validators.minLength(3)]],
      tenantId: [{ value: this.generatedTenantId, disabled: true }],
      firstname: ['', [Validators.required, Validators.minLength(4)]],
      lastname: ['', [Validators.required, Validators.minLength(4)]],
      adminEmail: ['', [Validators.required, Validators.email]]
    });
  }


  // Getter for easy form access in template
  get f() {
    return this.tenantForm.controls;
  }

  onSubmit() {
    this.isSubmitted = true;

    if (this.tenantForm.invalid) {
      return;
    }

    // Here you would typically call your service to create the tenant
    console.log('Form submitted:', this.tenantForm.getRawValue());
    this.showSuccess = true;

    // Reset form after 3 seconds and redirect
    setTimeout(() => {
      this.showSuccess = false;
      this.router.navigate(['/tenants']);
    }, 3000);
  }

  onCancel() {
    this.router.navigate(['/tenants']);
  }
}
