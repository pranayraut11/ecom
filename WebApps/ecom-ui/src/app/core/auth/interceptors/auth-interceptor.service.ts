import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpParams, HttpRequest, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { tap } from "rxjs";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { SpinnerService } from "../../services/spinner-service";
import { AuthService } from "../services/auth.service";
import { Route, Router } from "@angular/router";
import { TokenUtil } from "src/app/utils/TokenUtil";


@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

    constructor(private spinnerService: SpinnerService,private router: Router) {

    }
    intercept(req: HttpRequest<any>, next: HttpHandler) {

        let isSecured = true;
        if (isSecured) {
            console.log("In inter");
            
            this.spinnerService.show();
            let header = req.headers.get('X-Tenant-ID');
            console.log("Header in auth" + header);
            //Check local storage for token
            if(TokenUtil.isTokenValid()){
                let tokenDetailsJson = localStorage.getItem("token");
                console.log("Token in auth" + tokenDetailsJson);
                if (tokenDetailsJson) {
                    const tokenDetails: {
                        'access_token': string,
                        'refresh_token': string,
                        'expires_in': number
                    } = JSON.parse(tokenDetailsJson);
                    if (tokenDetails.access_token) {
                        const modifiedReq = req.clone({
                            setHeaders: {
                                'Authorization': 'Bearer ' + tokenDetails.access_token
                            }
                        });
                        return next.handle(modifiedReq).pipe(tap((event: HttpEvent<any>) => {
                            if (event instanceof HttpResponse) {
                                this.spinnerService.hide();
                            }
                        }, (error) => {
                            this.spinnerService.hide();
                        }));
                    }

                }
            }else{
                this.router.navigate['/']; 
            }
        }
        this.spinnerService.hide();
        return next.handle(req);
    }
}
