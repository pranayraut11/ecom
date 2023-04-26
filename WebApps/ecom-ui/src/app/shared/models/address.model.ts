export class Address {
    id:string
    firstName: string
    lastName: string
    addressLine1: string
    addressLine2: string
    landmark: string
    city: string
    state: string
    pincode: string
    mobile:string
    type: string
    defaultAddress:boolean
    constructor(id:string,
        firstName: string,
        lastName: string,
        addressLine1: string,
        addressLine2: string,
        landmark: string,
        city: string,
        state: string,
        pincode: string,
        mobile:string,
        type: string,
        defaultAddress:boolean
    ) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.addressLine1 = addressLine1
        this.addressLine2 = addressLine2
        this.landmark = landmark
        this.city = city
        this.state = state
        this.pincode = pincode
        this.mobile = mobile
        this.type = type
        this.defaultAddress = defaultAddress
    }
}