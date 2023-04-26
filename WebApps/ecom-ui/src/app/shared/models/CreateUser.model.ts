import { UserCredential } from "./UserCredential";

export class CreateUser {

    username: string;
    email: string;
    firstName: string;
    lastName: string;
    credentials: UserCredential[];
    enabled: boolean;

    constructor(
        username: string,
        email: string,
        firstName: string,
        lastName: string,
        credentials: UserCredential[],enabled: boolean
    ) {
        this.username = username
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.credentials = credentials
        this.enabled = enabled;
    }

}