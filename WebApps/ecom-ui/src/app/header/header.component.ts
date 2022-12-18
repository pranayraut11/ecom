import { Component, OnInit } from "@angular/core";
import { RolesDirective } from "../core/directives/roles.directive";
import { AuthRestService } from "../shared/services/rest-services/auth-rest-service";

@Component({
    selector: "app-header",
    templateUrl: "./header.component.html",
    viewProviders: [RolesDirective]})
export class HeaderComponent implements OnInit {
    
   
    constructor(private authService : AuthRestService){
       
    }
    ngOnInit() {
     
    }

    
}