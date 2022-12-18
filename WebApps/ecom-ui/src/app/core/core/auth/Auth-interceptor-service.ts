import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpParams, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthRestService } from "src/app/shared/services/rest-services/auth-rest-service";


@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

    constructor(authService: AuthRestService) {

    }
    intercept(req: HttpRequest<any>, next: HttpHandler) {
        let url = req.url;
        if (url.includes("/auth/login")) {
            return next.handle(req);
        }
        let tokenDetailsJson = localStorage.getItem("token");
        if (tokenDetailsJson) {
            const tokenDetails: {
                'access_token': string,
                'refresh_token': string,
                'expires_in': number
            } = JSON.parse(localStorage.getItem("token"));
            const modifiedReq = req.clone({
                headers: req.headers.set('Authorization', 'Bearer ' + tokenDetails.access_token)
            });
            return next.handle(modifiedReq);
        }
        return next.handle(req);
    }
}