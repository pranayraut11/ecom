import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/core/auth/Auth-Service';
import { SpinnerService } from './core/services/spinner-service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule,
    LoadingSpinnerComponent
  ]
})
export class AppComponent implements OnInit {
  constructor(private authService : AuthService, public spinnerService: SpinnerService){

  }
  title = 'ecom-ui';
  ngOnInit(): void {
      this.authService.autoLogin();
  }
 
}
