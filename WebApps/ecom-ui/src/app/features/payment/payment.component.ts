import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    CommonModule
  ]
})
export class PaymentComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
