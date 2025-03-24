import { Component, OnInit,Input } from '@angular/core';
import { Menu } from '../../models/menu.model';
import { MenusRestService } from '../../services/rest-services/menus-rest-service';

@Component({
  selector: 'app-menubar',
  templateUrl: './menubar.component.html',
  styleUrls: ['./menubar.component.css']
})
export class MenubarComponent implements OnInit {

  categories : Menu[];
  constructor(private menuRest: MenusRestService) { }

  ngOnInit(): void {
    this.loadMenu()
  }


  loadMenu() {
    console.log("Menus")
    this.menuRest.getMenus().subscribe((response) => {
      this.categories = response;
      console.log( this.categories)
    })
  }
}
