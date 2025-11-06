import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { HttpClientModule } from '@angular/common/http';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';
import { AuthService } from './features/auth/services/auth.service';
import { expect } from '@jest/globals';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let routerMock: any;
  let sessionServiceMock: any;

  beforeEach(async () => {
    routerMock = { navigate: jest.fn() };
    sessionServiceMock = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([]), HttpClientModule, MatToolbarModule],
      declarations: [AppComponent],
      providers: [
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: { snapshot: {}, params: of({}) } },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: AuthService, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should return observable from $isLogged', (done) => {
    component.$isLogged().subscribe(value => {
      expect(value).toBe(true);
      done();
    });
  });

  it('should log out and navigate to home', () => {
    component.logout();
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['']);
  });
});
