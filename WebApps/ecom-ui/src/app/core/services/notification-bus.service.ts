import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface NotificationEvent {
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  title?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationBusService {
  private notificationSubject = new Subject<NotificationEvent>();
  notifications$ = this.notificationSubject.asObservable();

  success(message: string, title?: string) {
    this.notify({ type: 'success', message, title });
  }

  error(message: string, title?: string) {
    this.notify({ type: 'error', message, title });
  }

  warning(message: string, title?: string) {
    this.notify({ type: 'warning', message, title });
  }

  info(message: string, title?: string) {
    this.notify({ type: 'info', message, title });
  }

  private notify(event: NotificationEvent) {
    this.notificationSubject.next(event);
  }
}
