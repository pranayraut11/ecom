import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-back-create-button',
  templateUrl: './back-create-button.component.html',
  styleUrls: ['./back-create-button.component.css']
})
export class BackCreateButtonComponent implements OnInit {

  @Input() pathFromParent: string;

  constructor(private route: Router) { }

  ngOnInit(): void {
  }

  goBack() {
    var currentPath = this.route.url;
    console.log(currentPath);
    var startIndex = currentPath.lastIndexOf('/');
    var navigateToPath = currentPath.substring(0, startIndex);
    this.route.navigate([navigateToPath]);
  }

  create() {
    this.route.navigate([this.pathFromParent]);
  }
}
