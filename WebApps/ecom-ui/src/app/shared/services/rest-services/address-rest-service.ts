import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { switchMap } from 'rxjs/operators';
import { environment } from "src/environments/environment";
import { ADDRESS, USER_SERVICE } from "../../constants/ApiEndpoints";
import { Address } from "../../models/address.model";

@Injectable({providedIn:"root"})
export class AddressRestService{

    constructor(private rest : HttpClient){

    }

    private unsetOtherDefaultAddresses() {
        return this.rest.post(environment.baseURL+USER_SERVICE+'/'+ADDRESS+'/unset-default', {});
    }

    addOrUpdateAddress(address: Address) {
        if (address.defaultAddress) {
            // First unset any existing default addresses
            return this.unsetOtherDefaultAddresses().pipe(
                switchMap(() => this.rest.post(environment.baseURL+USER_SERVICE+'/'+ADDRESS, address))
            );
        }
        return this.rest.post(environment.baseURL+USER_SERVICE+'/'+ADDRESS, address);
    }

    getAllAddress() : Observable<Address[]>{
        return this.rest.get<Address[]>(environment.baseURL+USER_SERVICE+'/'+ADDRESS);
    }

    deleteAddress(id: string){
       return this.rest.delete(environment.baseURL+USER_SERVICE+'/'+ADDRESS+'/'+id)
    }

    getAddress(id: string) : Observable<Address>{
        return this.rest.get<Address>(environment.baseURL+USER_SERVICE+'/'+ADDRESS+'/'+id);
    }

}