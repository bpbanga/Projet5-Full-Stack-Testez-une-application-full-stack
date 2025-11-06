import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { MeComponent } from './me.component';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { SessionService } from '../../services/session.service';
import { of } from 'rxjs';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { expect } from '@jest/globals';


describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: jest.Mocked<UserService>;
  let matSnackBar: jest.Mocked<MatSnackBar>;
  let router: jest.Mocked<Router>;
  let sessionService: Partial<SessionService>;

  beforeEach(async () => {
    // Mocks
    const userServiceMock = {
      getById: jest.fn().mockReturnValue(of({ id: 1, name: 'Test User' } as any)),
      delete: jest.fn().mockReturnValue(of(null))
    };
    const matSnackBarMock = { open: jest.fn() };
    const routerMock = { navigate: jest.fn() };
    sessionService = {
      sessionInformation: {
        id: 1,
        token: '',
        type: '',
        username: '',
        firstName: '',
        lastName: '',
        admin: false
      },
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionService },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA] // pour les mat-card, mat-icon, etc.
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    userService = TestBed.inject(UserService) as jest.Mocked<UserService>;
    matSnackBar = TestBed.inject(MatSnackBar) as jest.Mocked<MatSnackBar>;
    router = TestBed.inject(Router) as jest.Mocked<Router>;

    fixture.detectChanges(); // déclenche ngOnInit
  });

  it('should initialize user on ngOnInit', () => {
    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual({ id: 1, name: 'Test User' });
  });

  it('should delete user account and log out', fakeAsync(() => {
    // Act
    component.delete();
    tick();

    // Assert
    expect(userService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  }));
});
