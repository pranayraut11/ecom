import { Price } from "./price.model";

export class Product {
    public id: string;
    public name: string;
    public description: string;
    public price: Price;
    public images: string[];
    public imageUrl: string;

    constructor(id: string,name: string, description: string, price: Price,images: string[],imageUrl: string) {
        this.id = id;        
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        this.imageUrl = imageUrl;
    }

}