import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductTemplateComponent } from './product-template.component';

describe('ProductCreateComponent', () => {
  let component: ProductTemplateComponent;
  let fixture: ComponentFixture<ProductTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductTemplateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
