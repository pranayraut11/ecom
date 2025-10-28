import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Login } from '../../models/Login.model';
import { environment } from 'src/environments/environment';
import { USER_LOGIN, USER_LOGOUT, USER_SERVICE } from '../../constants/ApiEndpoints';
import { Token } from '../../models/Token';
import { AUTH_TOKEN } from '../../constants/AuthConst';

@Injectable({
  providedIn: 'root'
})
export class AuthRestService {

  constructor(private rest: HttpClient) { }
  

  login(login: Login) {
    console.log("In rest service")
    const headers = new HttpHeaders()
     .set("X-Tenant-ID", "ecom");
    return this.rest.post<Token>(environment.baseURL +'auth/'+USER_LOGIN, login,{headers});
      
  }

  logout() {
    return this.rest.get(environment.baseURL + USER_SERVICE+'/'+'auth/'+USER_LOGOUT);
  }

}
