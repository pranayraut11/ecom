import { Directive, ElementRef, HostBinding, Input, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../core/auth/Auth-Service';

@Directive({
  selector: '[appRoles]'
})
export class RolesDirective implements OnInit {

  @Input() hasRole: string;
  @HostBinding('style.display') display: string = 'none';

  private userSub: Subscription;
  constructor(private el: ElementRef, private authService: AuthService) { }

  ngOnInit() {
    this.userSub = this.authService.user.subscribe(response => {
      console.log("In has role directive");
      var roles = this.hasRole.toString().split(",");
      var matched = roles.find(ft => ft == "admin")
      if (matched) {
        this.display = 'block';
      }
    });
    console.log("Has role " + this.hasRole);
    this.hasRole;
  }

}
