import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/core/auth/Auth-Service';

@Component({
  selector: 'app-seller-header',
  templateUrl: './seller-header.component.html',
  styleUrls: ['./seller-header.component.css']
})
export class SellerHeaderComponent implements OnInit {

  isAuthenticated : boolean;

  constructor(private authService : AuthService) { }

  ngOnInit(): void {
    if(!this.authService.isTokenExpired){
      this.isAuthenticated = true;
    }else{
      this.isAuthenticated = true;
    }
  }

}
