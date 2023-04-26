import { Injectable } from "@angular/core";
import { BehaviorSubject, catchError, tap, throwError } from "rxjs";
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
    tokenDetails: Token;
    login(login: Login) {
        return this.authRest.login(login).pipe(catchError(errorRes => {
            let errorMessage = "Unknown error";
            console.log(errorRes);
            return throwError(() => errorMessage);
        }), tap(resData => {
            const tokenExpirationDate = new Date(new Date().getTime() + resData.expires_in * 1000);
            const user = new UserTokenDetails(resData.access_token, resData.refresh_token, tokenExpirationDate,resData.roles);
            localStorage.setItem(AUTH_TOKEN, JSON.stringify(resData));
            this.user.next(user);
        }));
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
        const tokenDetails = localStorage.getItem(AUTH_TOKEN);
        if (tokenDetails) {
            const tokenDetailsJons: {
                'access_token': string,
                'refresh_token': string,
                'expires_in': number,
                'roles': string[]
            } = JSON.parse(tokenDetails);
            if (tokenDetails) {
                const tokenExpirationDate = new Date(new Date().getTime() + tokenDetailsJons.expires_in * 1000);
                if (!this.isTokenExpired(tokenExpirationDate)) {
                    const user = new UserTokenDetails(tokenDetailsJons.access_token, tokenDetailsJons.refresh_token, tokenExpirationDate,tokenDetailsJons.roles);
                    this.user.next(user);
                }
            }
        }
        
    }

    isTokenExpired(tokenExpirationDate: Date) {
        let currentDateTime = new Date();
        console.log(currentDateTime.getTime());
        console.log(tokenExpirationDate.getTime());
        if (currentDateTime.getTime() > tokenExpirationDate.getTime()) {
            return true;
        }
        return false;
    }

}