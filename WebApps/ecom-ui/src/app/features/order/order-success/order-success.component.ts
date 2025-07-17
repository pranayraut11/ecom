import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-order-success',
  templateUrl: './order-success.component.html',
  styleUrls: ['./order-success.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class OrderSuccessComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
