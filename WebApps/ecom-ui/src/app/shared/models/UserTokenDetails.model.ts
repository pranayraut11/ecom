export class UserTokenDetails {
    constructor(
        public token: string,
        public refreshToken : string,
        public tokenExpirationDate: Date,
        public roles: string[]
    ) {

    }
}