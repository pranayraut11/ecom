import { CommonModule } from '@angular/common';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-filter',
  templateUrl: './product-filter.component.html',
  styleUrls: ['./product-filter.component.css'],
  standalone: true,
  imports: [FormsModule,CommonModule]
})
export class ProductFilterComponent {
  @Input() categories: string[] = [];
  @Input() selectedCategory: string = '';
  @Input() minPrice: number = 0;
  @Input() maxPrice: number = 5000;
  @Input() selectedBrands: string[] = [];
  @Input() brands: string[] = [];

  @Output() categoryChange = new EventEmitter<string>();
  @Output() priceChange = new EventEmitter<{min: number, max: number}>();
  @Output() brandsChange = new EventEmitter<string[]>();

  onCategoryChange(category: string) {
    this.categoryChange.emit(category);
  }

  onPriceChange() {
    this.priceChange.emit({ min: this.minPrice, max: this.maxPrice });
  }

  onBrandToggle(brand: string, checked: boolean) {
    if (checked) {
      this.selectedBrands = [...this.selectedBrands, brand];
    } else {
      this.selectedBrands = this.selectedBrands.filter(b => b !== brand);
    }
    this.brandsChange.emit(this.selectedBrands);
  }

  clearFilters() {
    this.categoryChange.emit('');
    this.priceChange.emit({ min: 0, max: 5000 });
    this.brandsChange.emit([]);
  }
}
