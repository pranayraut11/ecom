import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackCreateButtonComponent } from './back-create-button.component';

describe('BackCreateButtonComponent', () => {
  let component: BackCreateButtonComponent;
  let fixture: ComponentFixture<BackCreateButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BackCreateButtonComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BackCreateButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
