import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';

// ✅ Création d’un mock d’AuthService
class MockAuthService {
  login() {
    return of({
      token: 'fakeToken',
      type: 'BASIC',
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    } as SessionInformation);
  }
}


describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        SessionService,
        { provide: AuthService, useClass: MockAuthService } // utilisation du mock
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ✅ Test 1 : Connexion réussie
  it('should call authService.login and navigate on success', () => {
  const mockResponse: SessionInformation = {
    token: '123',
    type: 'BASIC',
    id: 1,
    username: 'demoUser',
    firstName: 'Demo',
    lastName: 'User',
    admin: false
  };

  const spyLogin = jest.spyOn(authService, 'login').mockReturnValue(of(mockResponse));
  const spyNavigate = jest.spyOn(component['router'], 'navigate');

  component.form.setValue({ email: 'demo@test.com', password: '123456' });
  component.submit();

  expect(spyLogin).toHaveBeenCalledWith({ email: 'demo@test.com', password: '123456' });
  expect(spyNavigate).toHaveBeenCalledWith(['/sessions']);
  expect(component.onError).toBe(false);
});


  // ✅ Test 2 : Erreur login
  it('should set onError to true if login fails', () => {
    jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('Invalid credentials')));

    component.form.setValue({ email: 'wrong@test.com', password: 'badpass' });
    component.submit();

    expect(component.onError).toBe(true);
  });

  // ✅ Test 3 : Champ obligatoire manquant
  it('should mark form invalid if required fields are empty', () => {
    component.form.setValue({ email: '', password: '' });

    expect(component.form.invalid).toBe(true);
    expect(component.form.controls['email'].hasError('required')).toBe(true);
    expect(component.form.controls['password'].hasError('required')).toBe(true);
  });
});
