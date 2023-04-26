import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { ADDRESS_CREATE } from "../../constants/ApiEndpoints";
import { Address } from "../../models/address.model";

@Injectable({providedIn:"root"})
export class AddressRestService{

    constructor(private rest : HttpClient){

    }

    addOrUpdateAddress(address: Address){
        return this.rest.post(environment.baseURL+ADDRESS_CREATE,address);
    }

    getAllAddress() : Observable<Address[]>{
        return this.rest.get<Address[]>(environment.baseURL+ADDRESS_CREATE);
    }

    deleteAddress(id: string){
       return this.rest.delete(environment.baseURL+ADDRESS_CREATE+'/'+id)
    }

    getAddress(id: string) : Observable<Address>{
        return this.rest.get<Address>(environment.baseURL+ADDRESS_CREATE+'/'+id);
    }

}