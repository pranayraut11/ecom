export interface ProductPrice {
    price: number;
    originalPrice?: number;
    discount?: number;
}

export class Product {
    public id: string;
    public name: string;
    public description: string;
    public price: ProductPrice;
    public images: string[];
    public category?: string;
    public brand?: string;
    public rating?: number;
    public createdAt?: string;

    constructor(
        id: string,
        name: string, 
        description: string, 
        price: ProductPrice,
        images: string[],
        category?: string,
        brand?: string,
        rating?: number,
        createdAt: string = new Date().toISOString()
    ) {
        this.id = id;        
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        this.category = category;
        this.brand = brand;
        this.rating = rating;
        this.createdAt = createdAt;
    }
}