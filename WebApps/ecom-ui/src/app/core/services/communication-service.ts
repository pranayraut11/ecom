import { Injectable } from "@angular/core";
import { BehaviorSubject, Subject } from "rxjs";

@Injectable({ providedIn: 'root'})
export class CommunicationService{
    emitData :  BehaviorSubject<any>;
    constructor(){
        this.emitData = new BehaviorSubject(null);
    }
    
    addData(data : any){
        this.emitData.next(data);
    }
}