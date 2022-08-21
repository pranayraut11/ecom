export class Order {
    private productName: string;
    private image: string;
    private price: number;
    private deliveryDate: Date;
    private deliveryStatus: string;

 

	constructor($productName: string, $image: string, $price: number, $deliveryDate: Date, $deliveryStatus: string) {
		this.productName = $productName;
		this.image = $image;
		this.price = $price;
		this.deliveryDate = $deliveryDate;
		this.deliveryStatus = $deliveryStatus;
	}
	
}