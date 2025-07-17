export class SubCategory{
    id: string;
    name: string;
    amount: string;
    prefix: string;
    postfix: string;
    imageUrl: string;
    url: string;
constructor(id: string,
    name: string,
    amount: string,
    prefix: string,
    postfix: string,
    imageUrl: string,
    url?: string){
    this.id=id;
    this.name= name;
    this.amount = amount;
    this.prefix=prefix;
    this.postfix= postfix;
    this.imageUrl = imageUrl;
    this.url = url || '';
}

}