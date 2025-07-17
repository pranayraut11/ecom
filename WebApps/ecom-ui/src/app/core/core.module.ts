import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptorService } from './auth/interceptors/auth-interceptor.service';
import { RolesDirective } from './directives/roles.directive';

@NgModule({
  declarations: [
    // Non-standalone directives
  ],
  imports: [
    CommonModule,
    
    // Standalone components
    RolesDirective
  ],
  exports: [
    // Export directives
    RolesDirective
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    }
  ]
})
export class CoreModule { }
