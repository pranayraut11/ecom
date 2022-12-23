export class Token{
    public access_token : string;
    public refresh_token : string;
    public expires_in : number;
    public roles : string[];

    constructor(access_token : string,refresh_token : string,roles: string[]){
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.roles = roles;
    }
}