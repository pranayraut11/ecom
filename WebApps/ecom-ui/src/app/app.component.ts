import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/core/auth/Auth-Service';
import { SpinnerService } from './core/services/spinner-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private authService : AuthService,public spinnerService: SpinnerService){

  }
  title = 'ecom-ui';
  ngOnInit(): void {
      this.authService.autoLogin();
  }
 
}
