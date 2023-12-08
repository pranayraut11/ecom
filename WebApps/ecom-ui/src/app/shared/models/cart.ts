import { CartProduct } from "./cart.product.model";

export class Cart {

    public id: string;
    public total: number;
    public totalPrice: number;
    public discount: number;
    public products: CartProduct[];
    public deliveryCharges: number;



    constructor(id: string, total: number, totalPrice: number, discount: number, products: CartProduct[],deliveryCharges: number) {
        this.id = id;
        this.total = total;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.products = products;
        this.deliveryCharges = deliveryCharges;

    }
    

}