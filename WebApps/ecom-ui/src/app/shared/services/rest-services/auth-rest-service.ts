import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Login } from '../../models/Login.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthRestService {

  constructor(private rest: HttpClient) { }

  login(login: Login){
      
    const params = new HttpParams({
      fromObject: {
        grant_type: 'password',
        username: login.username,
        password: login.password
      }
    });

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': 'Basic ' + window.btoa(environment.client_id + ':' +environment.client_secret)
      })
    };
    return this.rest.post(environment.authServerBaseURL+'realms/ecom/protocol/openid-connect/token',params,httpOptions);
  }
}
