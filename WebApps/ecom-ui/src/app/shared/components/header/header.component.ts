import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { RolesDirective } from "src/app/core/directives/roles.directive";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";


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