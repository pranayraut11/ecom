import { Component, OnInit } from "@angular/core";
import { RolesDirective } from "src/app/core/directives/roles.directive";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { AUTH_TOKEN } from "../../constants/AuthConst";


@Component({
    selector: "app-header",
    templateUrl: "./header.component.html",
    viewProviders: [RolesDirective],
    styleUrls: ['./header.component.css']})
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