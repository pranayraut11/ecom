import { Component, OnInit } from '@angular/core';
import { Order } from '../../../../shared/models/Order.model';
import { OrderRestService } from '../../../../shared/services/rest-services/order-rest-service';

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.css']
})
export class CreateOrderComponent implements OnInit {

  orderProduct : Order[];

  constructor(private orderRestService: OrderRestService) { }

  ngOnInit(): void {
    this.orderRestService.getOrders().subscribe(response=>{
      this.orderProduct = response;
    });
  }

}
