import { Directive, ElementRef, HostBinding, HostListener, Input, OnChanges, OnInit } from '@angular/core';
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
      if (this.hasRole) {
        var authRoles = this.hasRole.toString().split(",");
        if (response) {
          var matchedRole = response.roles.find(userRole => authRoles.find(authRole => authRole === userRole));
          if (matchedRole) {
            this.display = 'block';
          }
        }
      }
    });
  }
  // @HostListener('click', ['$event']) onClick($event) {
  //   console.info('clicked: ' + $event);
  //   this.display = 'none';
  // }

  ngOnDestroy() {
    this.userSub.unsubscribe();
  }

}
