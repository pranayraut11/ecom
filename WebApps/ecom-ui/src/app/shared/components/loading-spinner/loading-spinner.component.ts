import { Component, Injectable } from "@angular/core";
import { CommonModule } from "@angular/common";

@Component({
    selector:'app-loading-spinner',
    template:'<div class="lds-roller"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>',
    styleUrls:['./loading.spinner.component.css'],
    standalone: true,
    imports: [CommonModule]
})
export class LoadingSpinnerComponent{

}