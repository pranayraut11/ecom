export class UserCredential {

    type: string;
    value: string;

    constructor(type: string, value: string) {
        this.type = type
        this.value = value
    }
}