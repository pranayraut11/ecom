import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { USER_CREATE, USER_SERVICE } from "../../constants/ApiEndpoints";
import { Address } from "../../models/address.model";
import { CreateUser } from "../../models/CreateUser.model";

@Injectable({ providedIn: "root" })
export class UserRestService {


    constructor(private rest: HttpClient) {
    }

    registerUser(userDetails: CreateUser){
        const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
       return this.rest.post(environment.baseURL+''+USER_CREATE,userDetails,{headers});
    }

   
}