import { Injectable } from "@angular/core";
import { BehaviorSubject, Subscription, catchError, tap, throwError } from "rxjs";
import { AUTH_TOKEN } from "src/app/shared/constants/AuthConst";
import { Login } from "src/app/shared/models/Login.model";
import { Token } from "src/app/shared/models/Token";
import { UserTokenDetails } from "src/app/shared/models/UserTokenDetails.model";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { TokenUtil } from "src/app/utils/TokenUtil";

interface JwtPayload {
  exp: number;  // Expiration time
  iat?: number; // Issued at (optional)
  sub?: string; // Subject (optional)
}

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
        console.log("in auth service")
        return this.authRest.login(login).pipe(catchError(errorRes => {
            let errorMessage = "Unknown error";
            console.log(errorRes);
            return throwError(() => errorMessage);
        }), tap(resData => {
            console.log("Logged in user "+resData);
            const user = new UserTokenDetails(resData.access_token, resData.refresh_token,null,resData.roles);
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

    // autoLogin() {
    //      const tokenDetails = localStorage.getItem(AUTH_TOKEN);
    //      const tokenDetailsJons =  TokenUtil.getTokenDetails(tokenDetails);
    //     if (!TokenUtil.isTokenExpired1(tokenDetailsJons.access_token,tokenDetailsJons.refresh_token)) {
           
    //         const user = new UserTokenDetails(tokenDetailsJons.access_token, tokenDetailsJons.refresh_token, TokenUtil.getExpirationDate(tokenDetailsJons.expires_in),tokenDetailsJons.roles);
    //         this.user.next(user);
    //     }
        
    // }
}
