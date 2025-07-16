import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class SearchComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
