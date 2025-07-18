import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { HeaderComponent } from 'src/app/shared/components/header/header.component';
import { MenubarComponent } from 'src/app/shared/components/menubar/menubar.component';
import { FooterComponent } from 'src/app/shared/components/footer/footer.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [
    RouterModule,
    HeaderComponent,
    MenubarComponent,
    FooterComponent
  ]
})
export class HomeComponent implements OnInit {
  constructor() { }

  ngOnInit(): void {
    
  }
}
