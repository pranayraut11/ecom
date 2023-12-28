export class Page {

    totalElements: number;
    totalPages: number
    first: boolean;
    last: boolean;
    size: number
    number: number
    data = [];


	constructor($totalElements: number,$totalPages:number, $first: boolean, $last: boolean,$size:number,$number:number) {
		this.totalElements = $totalElements;
		this.first = $first;
		this.last = $last;
        this.totalPages=$totalPages;
        this.size=$size;
        this.number=$size;
	}



}