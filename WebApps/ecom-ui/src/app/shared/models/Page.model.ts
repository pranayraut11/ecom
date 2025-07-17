export class Page {
    content: any[];
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    size: number;
    number: number;
    numberOfElements: number;
    empty: boolean;

    constructor(
        content: any[] = [],
        totalElements: number = 0,
        totalPages: number = 0,
        first: boolean = true,
        last: boolean = true,
        size: number = 10,
        number: number = 0,
        numberOfElements: number = 0,
        empty: boolean = true
    ) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.size = size;
        this.number = number;
        this.numberOfElements = numberOfElements;
        this.empty = empty;
    }



}