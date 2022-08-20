import { CartProduct } from "./cart.product.model";

export class Cart {

    public id: string;
    public userId: string;
    public total: number;
    public totalPrice: number;
    public discount: number;
    public products: CartProduct[];



    constructor(id: string, userId: string, total: number, totalPrice: number, discount: number, products: CartProduct[]) {
        this.id = id;
        this.userId = userId;
        this.total = total;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.products = products;

    }

}