export class CartProduct {

    public orderId: string;

    public productId: string;

    public name: string

    public image: string

    public price: number

    public discountedPrice: number;

    public discount: number;

    public quantity: number;

    public deliveryCharge: number;
    
    constructor(orderId: string,productId: string, name: string, image: string, price: number, discountedPrice: number, discount: number, quantity: number,deliveryCharge: number) {
        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.deliveryCharge = deliveryCharge;
    }
}