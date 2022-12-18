import { Component, OnInit } from "@angular/core";
import { Subscribable, Subscription } from "rxjs";
import { AuthRestService } from "../shared/services/rest-services/auth-rest-service";

@Component({
    selector: "app-header",
    templateUrl: "./header.component.html"
})
export class HeaderComponent implements OnInit {
    
    
    constructor(private authService : AuthRestService){
       
    }
    ngOnInit() {
     
    }

    
}