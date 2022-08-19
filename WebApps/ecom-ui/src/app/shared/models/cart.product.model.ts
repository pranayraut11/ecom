export class CartProduct {
    public productId: string;

    public name: string

    public image: string

    public price: number

    public discountedPrice: number;

    public discount: number;

    public quantity: number;
    constructor(productId: string, name: string, image: string, price: number, discountedPrice: number, discount: number, quantity: number) {
        this.productId = productId;
        this.name = name;
        this.image = image;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.discount = discount;
        this.quantity = quantity;
    }
}