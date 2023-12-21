export class Category{
    id: string;
    name: string;
    amount : number;
    prefix: string;
    postfix: string;
constructor(id: string,
    name: string,
    amount : number,
    prefix: string,
    postfix: string){
    this.id=id;
    this.name= name;
    this.amount = amount;
    this.prefix=prefix;
    this.postfix= postfix;
}

}