export class CreateOrder{
    public buyNow : boolean ;
    public id : string;

    constructor(buyNow : boolean, id : string ){
        this.buyNow = buyNow;
        this.id = id;
    }

}