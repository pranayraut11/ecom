import {jwtDecode} from "jwt-decode";
import { AUTH_TOKEN } from "../shared/constants/AuthConst";

export interface JwtPayload {
  exp: number;   // Expiry time (seconds since epoch)
  iat?: number;  // Issued at (optional)
  sub?: string;  // Subject (optional)
  [key: string]: any; // Allow other custom claims
}

export class TokenUtil {
  /**
   * Decode JWT token
   */
  static decodeToken(token: string): JwtPayload | null {
    if (!token) return null;
    try {
      return jwtDecode<JwtPayload>(token);
    } catch (e) {
      console.error("Invalid token", e);
      return null;
    }
  }

  /**
   * Check if token is expired
   */
  static isTokenExpired(token: string): boolean {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return true;

    const expiryTime = decoded.exp * 1000; // convert to ms
    return Date.now() > expiryTime;
  }

  /**
   * Get expiry time as Date
   */
  static getExpiryDate(token: string): Date | null {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return null;
    return new Date(decoded.exp * 1000);
  }

  /**
   * Get remaining time (in seconds) before expiry
   */
  static getRemainingTime(token: string): number {
    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) return 0;

    return decoded.exp - Math.floor(Date.now() / 1000);
  }

   static isTokenExpired1(accessToken: string, refreshToken?: string): boolean {
    if (!accessToken) return true;
  
    try {
      const decoded = jwtDecode<JwtPayload>(accessToken);
      if (!decoded.exp) return true;
  
      const accessExpiry = decoded.exp * 1000;
  
      // Case 1: Access token still valid
      if (Date.now() < accessExpiry) {
        return false; // token valid
      }
  
      // Case 2: Access token expired, check refresh token
      console.log("Access token expired at", new Date(accessExpiry));
  
      if (refreshToken) {
        const decodedRefresh = jwtDecode<JwtPayload>(refreshToken);
        const refreshExpiry = decodedRefresh.exp * 1000;
  
        if (Date.now() < refreshExpiry) {
          console.log("✅ Refresh token still valid, you can refresh");
          return true; // Access expired but refresh available
        } else {
          console.log("❌ Refresh token expired too");
          localStorage.removeItem(AUTH_TOKEN);
          return true; // Both expired
        }
      }
  
      return true; // fallback
    } catch (error) {
      return true; // invalid token
    }
  }
  
   static  isTokenValid(): boolean {
      console.log("checking auth token.")
      const token = localStorage.getItem(AUTH_TOKEN);
      console.log("Token .. "+token)
      return token ? !TokenUtil.isTokenExpired1(TokenUtil.getTokenDetails(token).access_token,TokenUtil.getTokenDetails(token).refresh_token) : false;
    }

     static getTokenDetails(tokenDetails:any){
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
}
