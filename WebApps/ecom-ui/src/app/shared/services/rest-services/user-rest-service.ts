import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";
import { ADDRESS_CREATE, USER_CREATE } from "../../constants/ApiEndpoints";
import { Address } from "../../models/address.model";
import { CreateUser } from "../../models/CreateUser.model";

@Injectable({ providedIn: "root" })
export class UserRestService {


    constructor(private rest: HttpClient) {
    }

    registerUser(userDetails: CreateUser){
        const headers = new HttpHeaders()
        .set("X-CustomHeader", "none");
       return this.rest.post(environment.localURL+USER_CREATE,userDetails,{headers});
    }

    addOrUpdateAddress(address: Address){
        return this.rest.post(environment.localURL+ADDRESS_CREATE,address);
    }
}