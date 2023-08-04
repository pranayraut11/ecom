import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductDetailedInfoComponent } from './product-detailed-info.component';

describe('ProductDetailedInfoComponent', () => {
  let component: ProductDetailedInfoComponent;
  let fixture: ComponentFixture<ProductDetailedInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductDetailedInfoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductDetailedInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
