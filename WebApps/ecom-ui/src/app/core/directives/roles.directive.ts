import { Directive, ElementRef, HostBinding, HostListener, Input, OnChanges, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';

@Directive({
  selector: '[appRoles]',
  standalone: true
})
export class RolesDirective implements OnInit {

  @Input() hasRole: string;
  @HostBinding('style.display') display: string = 'none';
  private userSub: Subscription;

  constructor(private el: ElementRef, private authService: AuthService) { }

  ngOnInit() {
    this.userSub = this.authService.user.subscribe(response => {
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
 
  ngOnDestroy() {
    this.userSub.unsubscribe();
  }

}
