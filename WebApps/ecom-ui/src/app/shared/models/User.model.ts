export class User {
    constructor(
        public token: string,
        public refreshToken : string,
        public tokenExpirationDate: Date
    ) {

    }
}