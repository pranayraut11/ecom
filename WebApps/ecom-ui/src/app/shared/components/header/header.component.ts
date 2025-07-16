import { Component, OnInit } from "@angular/core";
import { RolesDirective } from "src/app/core/directives/roles.directive";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { AUTH_TOKEN } from "../../constants/AuthConst";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { LoginComponent } from "../login/login.component";

@Component({
    selector: "app-header",
    templateUrl: "./header.component.html",
    styleUrls: ['./header.component.css'],
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        RolesDirective,
        LoginComponent
    ]
})
export class HeaderComponent implements OnInit {
    
    isAuthenticated:boolean=false;
    constructor(private authService : AuthRestService){
       
    }
    ngOnInit() {
       
        if(localStorage.getItem(AUTH_TOKEN)){
            console.log("User is authenticated")
            this.isAuthenticated=true;
        }
    }

    
}