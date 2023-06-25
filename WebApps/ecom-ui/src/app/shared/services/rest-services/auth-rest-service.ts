import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Login } from '../../models/Login.model';
import { environment } from 'src/environments/environment';
import { USER_LOGIN, USER_LOGOUT, USER_SERVICE } from '../../constants/ApiEndpoints';
import { Token } from '../../models/Token';

@Injectable({
  providedIn: 'root'
})
export class AuthRestService {

  constructor(private rest: HttpClient) { }
  

  login(login: Login) {
    const headers = new HttpHeaders()
     .set("X-CustomHeader", "none");
    return this.rest.post<Token>(environment.baseURL + USER_SERVICE+'/'+'auth/'+USER_LOGIN, login,{headers});
      
  }

  logout() {
    return this.rest.get(environment.baseURL + USER_SERVICE+'/'+'auth/'+USER_LOGOUT);
  }
}
