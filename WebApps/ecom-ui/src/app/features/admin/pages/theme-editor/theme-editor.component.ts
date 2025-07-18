import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ThemeService } from '../../../../core/services/theme.service';
import { UiTheme } from '../../../../core/models/ui-theme.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-theme-editor',
  templateUrl: './theme-editor.component.html',
  styleUrls: ['./theme-editor.component.css']
})
export class ThemeEditorComponent implements OnInit {
  themes: UiTheme[] = [];
  themeForm: FormGroup;
  selectedTheme: UiTheme | null = null;
  isEditing: boolean = false;
  isCreating: boolean = false;
  
  private baseUrl = `${environment.apiUrl}/ui-service/api/ui/themes`;
  
  constructor(
    private themeService: ThemeService,
    private formBuilder: FormBuilder,
    private http: HttpClient
  ) {
    this.themeForm = this.formBuilder.group({
      id: [''],
      name: ['', [Validators.required, Validators.minLength(3)]],
      primaryColor: ['#3f51b5', [Validators.required]],
      secondaryColor: ['#f50057', [Validators.required]],
      backgroundColor: ['#ffffff', [Validators.required]],
      textColor: ['#333333', [Validators.required]],
      linkColor: ['#3f51b5', [Validators.required]],
      headerBackgroundColor: ['#3f51b5', [Validators.required]],
      headerTextColor: ['#ffffff', [Validators.required]],
      footerBackgroundColor: ['#f5f5f5', [Validators.required]],
      footerTextColor: ['#333333', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadThemes();
  }

  loadThemes(): void {
    this.themeService.getAllThemes().subscribe(themes => {
      this.themes = themes;
    });
  }

  editTheme(theme: UiTheme): void {
    this.selectedTheme = theme;
    this.themeForm.patchValue(theme);
    this.isEditing = true;
    this.isCreating = false;
  }

  createNewTheme(): void {
    this.selectedTheme = null;
    this.themeForm.reset({
      primaryColor: '#3f51b5',
      secondaryColor: '#f50057',
      backgroundColor: '#ffffff',
      textColor: '#333333',
      linkColor: '#3f51b5',
      headerBackgroundColor: '#3f51b5',
      headerTextColor: '#ffffff',
      footerBackgroundColor: '#f5f5f5',
      footerTextColor: '#333333'
    });
    this.isCreating = true;
    this.isEditing = false;
  }

  cancelEdit(): void {
    this.selectedTheme = null;
    this.themeForm.reset();
    this.isEditing = false;
    this.isCreating = false;
  }

  applyTheme(theme: UiTheme): void {
    this.themeService.setCurrentTheme(theme);
  }

  saveTheme(): void {
    if (this.themeForm.invalid) {
      return;
    }

    const themeData = this.themeForm.value;
    
    if (this.isEditing && this.selectedTheme) {
      // Update existing theme
      this.http.put(`${this.baseUrl}/${this.selectedTheme.id}`, themeData)
        .subscribe(
          () => {
            this.loadThemes();
            this.cancelEdit();
          },
          error => {
            console.error('Error updating theme:', error);
          }
        );
    } else {
      // Create new theme
      this.http.post(this.baseUrl, themeData)
        .subscribe(
          () => {
            this.loadThemes();
            this.cancelEdit();
          },
          error => {
            console.error('Error creating theme:', error);
          }
        );
    }
  }

  deleteTheme(theme: UiTheme): void {
    if (confirm(`Are you sure you want to delete the theme "${theme.name}"?`)) {
      this.http.delete(`${this.baseUrl}/${theme.id}`)
        .subscribe(
          () => {
            this.loadThemes();
            if (this.selectedTheme && this.selectedTheme.id === theme.id) {
              this.cancelEdit();
            }
          },
          error => {
            console.error('Error deleting theme:', error);
          }
        );
    }
  }
}
