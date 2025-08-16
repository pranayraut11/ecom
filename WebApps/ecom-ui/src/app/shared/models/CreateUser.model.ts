import { UserCredential } from "./UserCredential";

export class CreateUser {

    username: string;
    email: string;
    mobile:string;
    firstName: string;
    lastName: string;
    password: string
    enabled: boolean;

    constructor(
        username: string,
        email: string,
        firstName: string,
        lastName: string,
        password:string,
        enabled: boolean,
        mobile:string

    ) {
        this.username = username
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.password = password
        this.enabled = enabled;
        this.mobile = mobile;
    }

}