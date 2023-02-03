import { Component, OnInit } from '@angular/core';
import { Order } from 'src/app/shared/models/Order.model';
import { Product } from 'src/app/shared/models/product.model';
import { OrderRestService } from 'src/app/shared/services/rest-services/order-rest-service';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class OrderListComponent implements OnInit {
  productList : Order[];
  
  constructor(private orderService : OrderRestService) { }
 
  ngOnInit(): void {
    this.orderService.getOrders().subscribe(res=>{
      this.productList = res;
    });
  }

}
