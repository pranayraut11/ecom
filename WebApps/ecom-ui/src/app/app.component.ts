import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/core/auth/Auth-Service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private authService : AuthService){

  }
  title = 'ecom-ui';
  ngOnInit(): void {
      this.authService.autoLogin();
  }
 
}
