export class CartProduct {

    public orderId: string;

    public productId: string;

    public name: string

    public image: string

    public price: number;
    public originalPrice?: number;
    public discount?: number;
    public quantity: number;
    public deliveryCharge: number;
    
    constructor(
        orderId: string,
        productId: string, 
        name: string, 
        image: string, 
        price: number,
        quantity: number,
        deliveryCharge: number = 0,
        originalPrice?: number,
        discount?: number
    ) {
        this.orderId = orderId;
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.quantity = quantity;
        this.deliveryCharge = deliveryCharge;
    }
}