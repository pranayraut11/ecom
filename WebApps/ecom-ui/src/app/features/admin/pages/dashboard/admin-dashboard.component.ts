import { Component, OnInit } from '@angular/core';
import { ThemeService } from '../../../../core/services/theme.service';
import { ComponentService } from '../../../../core/services/component.service';
import { LayoutService } from '../../../../core/services/layout.service';
import { UiTheme } from '../../../../core/models/ui-theme.model';
import { UiComponent } from '../../../../core/models/ui-component.model';
import { UiLayout } from '../../../../core/models/ui-layout.model';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  themeCount: number = 0;
  componentCount: number = 0;
  layoutCount: number = 0;
  activeComponentCount: number = 0;
  
  constructor(
    private themeService: ThemeService,
    private componentService: ComponentService,
    private layoutService: LayoutService
  ) { }

  ngOnInit(): void {
    this.loadCounts();
  }

  loadCounts(): void {
    this.themeService.getAllThemes().subscribe(themes => {
      this.themeCount = themes.length;
    });

    this.componentService.getAllComponents().subscribe(components => {
      this.componentCount = components.length;
      this.activeComponentCount = components.filter(c => c.active).length;
    });

    this.layoutService.getAllLayouts().subscribe(layouts => {
      this.layoutCount = layouts.length;
    });
  }
}
