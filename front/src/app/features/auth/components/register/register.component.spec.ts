import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

// Création d'un mock AuthService
class MockAuthService {
  register() {
    return of(void 0); // simulate successful registration
  }
}

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useClass: MockAuthService }
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ✅ Test 1 : Création de compte réussie
  it('should call authService.register and navigate to /login on success', () => {
    const spyRegister = jest.spyOn(authService, 'register').mockReturnValue(of(void 0));
    const spyNavigate = jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    component.form.setValue({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: '123456'
    });

    component.submit();

    expect(spyRegister).toHaveBeenCalledWith({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: '123456'
    });
    expect(spyNavigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  });

  // ✅ Test 2 : Affichage d’erreur si un champ obligatoire est vide
  it('should mark form invalid if required fields are empty', () => {
    component.form.setValue({
      email: '',
      firstName: '',
      lastName: '',
      password: ''
    });

    expect(component.form.invalid).toBe(true);
    expect(component.form.controls['email'].hasError('required')).toBe(true);
    expect(component.form.controls['firstName'].hasError('required')).toBe(true);
    expect(component.form.controls['lastName'].hasError('required')).toBe(true);
    expect(component.form.controls['password'].hasError('required')).toBe(true);
  });

  // ✅ Test 3 : Gestion d’erreur si l’API renvoie une erreur
  it('should set onError to true if registration fails', () => {
    jest.spyOn(authService, 'register').mockReturnValue(throwError(() => new Error('Failed')));

    component.form.setValue({
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: '123456'
    });

    component.submit();

    expect(component.onError).toBe(true);
  });
});
