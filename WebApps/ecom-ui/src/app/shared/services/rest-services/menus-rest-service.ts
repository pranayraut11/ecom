import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Menu } from "../../models/menu.model";

@Injectable({ providedIn: 'root' })
export class MenusRestService {

    constructor(private rest: HttpClient){

    }

    getMenus() : Observable<Menu[]>{
        const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
        console.log("Menus " +environment.baseURL+'menus')
        return this.rest.get<Menu[]>(environment.baseURL+'menus',{headers});
    }

}