import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Login } from '../../models/Login.model';
import { environment } from 'src/environments/environment';
import { USER_LOGIN, USER_LOGOUT } from '../../constants/ApiEndpoints';
import { Token } from '../../models/Token';
import { BehaviorSubject, catchError, Subject, tap, throwError } from 'rxjs';
import { User } from '../../models/User.model';

@Injectable({
  providedIn: 'root'
})
export class AuthRestService {

  constructor(private rest: HttpClient) { }
  

  login(login: Login) {
    return this.rest.post<Token>(environment.baseURL + USER_LOGIN, login);
      
  }

  logout(){
    return this.rest.get(environment.authServerBaseURL + USER_LOGOUT);
  }
}
