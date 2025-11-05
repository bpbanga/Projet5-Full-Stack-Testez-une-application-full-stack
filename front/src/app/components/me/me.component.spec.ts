import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { MeComponent } from './me.component';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { expect } from '@jest/globals';


describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockSessionService = {
    sessionInformation: { id: 1, admin: true },
    logOut: jest.fn()
  };

  const mockUser = { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@test.com' };

  beforeEach(async () => {
    const mockUserService = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of({}))
    };

    const mockRouter = { navigate: jest.fn() };
    const mockSnackBar = { open: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [HttpClientTestingModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    userService = TestBed.inject(UserService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user information on init', () => {
    expect(userService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should delete user account and log out', () => {
    component.delete();

    expect(userService.delete).toHaveBeenCalledWith('1');
    expect(matSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });
});
