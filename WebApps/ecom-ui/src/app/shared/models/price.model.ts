export class Price{
    public price : number;
    public discountedPrice : number;
    public discount: number;
    constructor(price : number,discountedPrice : number,discount: number){
        this.price = price;
        this.discount = discount;
        this.discountedPrice = discountedPrice;
    }
}