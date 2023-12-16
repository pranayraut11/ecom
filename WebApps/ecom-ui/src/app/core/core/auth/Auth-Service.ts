import { Injectable } from "@angular/core";
import { BehaviorSubject, Subscription, catchError, tap, throwError } from "rxjs";
import { AUTH_TOKEN } from "src/app/shared/constants/AuthConst";
import { Login } from "src/app/shared/models/Login.model";
import { Token } from "src/app/shared/models/Token";
import { UserTokenDetails } from "src/app/shared/models/UserTokenDetails.model";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";


@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private authRest: AuthRestService) {

    }
    user = new BehaviorSubject<UserTokenDetails>(null);
    private userSub: Subscription;
    tokenDetails: Token;
    login(login: Login) {
        return this.authRest.login(login).pipe(catchError(errorRes => {
            let errorMessage = "Unknown error";
            console.log(errorRes);
            return throwError(() => errorMessage);
        }), tap(resData => {
            console.log("Logged in user "+resData);
            const tokenExpirationDate = new Date(new Date().getTime() + resData.expires_in * 1000);
            const user = new UserTokenDetails(resData.access_token, resData.refresh_token, tokenExpirationDate,resData.roles);
            localStorage.setItem(AUTH_TOKEN, JSON.stringify(resData));
            this.user.next(user);
        }));
    }

    getUserRoles() : any[] {
        let roles : string[];
        this.userSub = this.user.subscribe(response => {
            if (this.userSub) {
              if (response) {
                roles = response.roles;
              }
            }
          });
          return roles;
    }

    logout() {
        return this.authRest.logout().pipe(catchError(errorRes => {
           console.log(errorRes.status);
           let errorMessage = "Unknown error";
           localStorage.removeItem(AUTH_TOKEN);
           return throwError(() => errorMessage);
        }),tap(resData => {
            this.user.unsubscribe();
        }));
    }

    autoLogin() {
        if (!this.isTokenExpired()) {
            const tokenDetails = localStorage.getItem(AUTH_TOKEN);
            const tokenDetailsJons =  this.getTokenDetails(tokenDetails);
            const user = new UserTokenDetails(tokenDetailsJons.access_token, tokenDetailsJons.refresh_token, this.getExpirationDate(tokenDetailsJons.expires_in),tokenDetailsJons.roles);
            this.user.next(user);
        }
        
    }

    getTokenDetails(tokenDetails:any){
        const tokenDetailsJons: {
            'access_token': string,
            'refresh_token': string,
            'expires_in': number,
            'roles': string[]
        } = JSON.parse(tokenDetails);
        return tokenDetailsJons;
    }

    getExpirationDate(expires_in:number){
        return new Date(new Date().getTime() + expires_in * 1000);
              
    }
    isTokenExpired() {
        const tokenDetails = localStorage.getItem(AUTH_TOKEN);
        console.log("isTokenExpired got "+tokenDetails);
        if (tokenDetails) {
            console.log("Token details found in storage "+tokenDetails);
            const tokenDetailsJons =  this.getTokenDetails(tokenDetails);
            if (tokenDetailsJons) {
                const tokenExpirationDate = this.getExpirationDate(tokenDetailsJons.expires_in);
                let currentDateTime = new Date();
                console.log(currentDateTime.getTime());
                console.log(tokenExpirationDate.getTime());
                if (currentDateTime.getTime() > tokenExpirationDate.getTime()) {
                    return true;
                }else{
                    return false; 
                }
            }
        }
       
        return true;
    }

}