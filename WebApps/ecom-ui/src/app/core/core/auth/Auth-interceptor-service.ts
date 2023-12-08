import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpParams, HttpRequest, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { tap } from "rxjs";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";
import { SpinnerService } from "../../services/spinner-service";


@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

    constructor(authService: AuthRestService, private spinnerService: SpinnerService) {

    }
    intercept(req: HttpRequest<any>, next: HttpHandler) {

        let isSecured = true;
        if(isSecured){
        this.spinnerService.show();
        // return next.handle(req).pipe(tap((event: HttpEvent<any>) => {
        //     if (event instanceof HttpResponse) {
        //         this.spinnerService.hide();
        //     }
        // }, (error) => {
        //     this.spinnerService.hide();
        // }));
        let header = req.headers.get("X-CustomHeader");
        console.log("Header in auth" + header);
        if (header == null) {
            let tokenDetailsJson = localStorage.getItem("token");
            if (tokenDetailsJson) {
                const tokenDetails: {
                    'access_token': string,
                    'refresh_token': string,
                    'expires_in': number
                } = JSON.parse(tokenDetailsJson);
                const modifiedReq = req.clone({
                    headers: req.headers.set('Authorization', 'Bearer ' + tokenDetails.access_token)
                });
                return next.handle(modifiedReq).pipe(tap((event: HttpEvent<any>) => {
                    if (event instanceof HttpResponse) {
                        this.spinnerService.hide();
                    }
                }, (error) => {
                    this.spinnerService.hide();
                }));
            }
        } else {
            
            return next.handle(req).pipe(tap((event: HttpEvent<any>) => {
                if (event instanceof HttpResponse) {
                    this.spinnerService.hide();
                }
            }, (error) => {
                this.spinnerService.hide();
            }));
        }
    }else{
        return next.handle(req).pipe(tap((event: HttpEvent<any>) => {
            if (event instanceof HttpResponse) {
               
            }
        }, (error) => {
           
        }));
    }
    }
}