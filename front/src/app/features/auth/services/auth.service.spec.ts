import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // vérifie qu'il n'y a pas de requêtes HTTP non traitées
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call POST /api/auth/register when register is called', () => {
    const mockRequest: RegisterRequest = {
      email: 'test@example.com',
      password: '123456',
      firstName: 'John',
      lastName: 'Doe'
    };

    service.register(mockRequest).subscribe(response => {
      expect(response).toBeUndefined(); // car on retourne Observable<void>
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);

    req.flush(null); // simule une réponse vide
  });

  it('should call POST /api/auth/login and return session information', () => {
    const mockLogin: LoginRequest = {
      email: 'test@example.com',
      password: '123456'
    };

    const mockResponse: SessionInformation = {
      id: 1,
      username: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
      token: 'jwt-token',
      type: 'Bearer'
    };

    service.login(mockLogin).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockLogin);

    req.flush(mockResponse); // simule une réponse du backend
  });
});
