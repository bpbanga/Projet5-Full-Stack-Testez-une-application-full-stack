import { TestBed } from '@angular/core/testing';
import { take } from 'rxjs/operators';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit false by default for $isLogged()', (done) => {
    service.$isLogged().pipe(take(1)).subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });

  it('should log in correctly', (done) => {
    const mockUser: SessionInformation = {
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      token: 'abc123',
      type: 'admin',
      admin: false
    };

    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);

    service.$isLogged().pipe(take(1)).subscribe(value => {
      expect(value).toBe(true);
      done();
    });
  });

  it('should log out correctly', (done) => {
    // Log in first
    const mockUser: SessionInformation = {
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      token: 'abc123',
      type: 'admin',
      admin: false
    };
    service.logIn(mockUser);

    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();

    service.$isLogged().pipe(take(1)).subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });
});
