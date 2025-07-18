import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NotificationBusService } from '../../core/services/notification-bus.service';

@Component({
  selector: 'app-notification-handler',
  template: '',
  standalone: true
})
export class NotificationHandlerComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  constructor(
    private toastr: ToastrService,
    private notificationBus: NotificationBusService
  ) {
    this.subscription = this.notificationBus.notifications$.subscribe(notification => {
      switch (notification.type) {
        case 'success':
          this.toastr.success(notification.message, notification.title);
          break;
        case 'error':
          this.toastr.error(notification.message, notification.title);
          break;
        case 'warning':
          this.toastr.warning(notification.message, notification.title);
          break;
        case 'info':
          this.toastr.info(notification.message, notification.title);
          break;
      }
    });
  }

  ngOnInit(): void {}

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
