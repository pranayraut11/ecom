import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";

@Injectable({ providedIn: 'root'})
export class SpinnerService{

    visiblity : BehaviorSubject<boolean>;


    constructor(){
        this.visiblity = new BehaviorSubject(false);
    }

    show(){
        this.visiblity.next(true);
    }

    hide(){
        this.visiblity.next(false);
    }

}