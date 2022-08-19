import { CartProduct } from "./cart.product.model";

export class Cart {

    public id: string;
    public userId: string;
    public products: CartProduct[];



    constructor(id: string, userId: string, products: CartProduct[]) {
        this.id = id;
        this.userId = userId;
        this.products = products;

    }

}