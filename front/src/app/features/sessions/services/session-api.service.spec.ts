import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { expect } from '@jest/globals';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService],
    });

    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ✅ all()
  it('should fetch all sessions', () => {
    const mockSessions: Session[] = [];
    service.all().subscribe(data => expect(data).toEqual(mockSessions));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  it('should handle error on all()', () => {
    service.all().subscribe({
      next: () => fail('should fail'),
      error: err => expect(err.status).toBe(500),
    });

    const req = httpMock.expectOne('api/session');
    req.flush({}, { status: 500, statusText: 'Server Error' });
  });

  // ✅ detail()
  it('should fetch session detail', () => {
    const mockSession: Session = { id: 1, name: 'Test', description: 'Desc', date: new Date(), teacher_id: 1, users: [] };
    service.detail('1').subscribe(data => expect(data).toEqual(mockSession));

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  it('should handle error on detail()', () => {
    service.detail('1').subscribe({
      next: () => fail('should fail'),
      error: err => expect(err.status).toBe(404),
    });

    const req = httpMock.expectOne('api/session/1');
    req.flush({}, { status: 404, statusText: 'Not Found' });
  });

  // ✅ create()
  it('should create a session', () => {
    const mockSession: Session = { id: 1, name: 'Test', description: 'Desc', date: new Date(), teacher_id: 1, users: [] };
    service.create(mockSession).subscribe(data => expect(data).toEqual(mockSession));

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    req.flush(mockSession);
  });

  it('should handle error on create()', () => {
    const mockSession: Session = { id: 1, name: 'Test', description: 'Desc', date: new Date(), teacher_id: 1, users: [] };
    service.create(mockSession).subscribe({
      next: () => fail('should fail'),
      error: err => expect(err.status).toBe(400),
    });

    const req = httpMock.expectOne('api/session');
    req.flush({}, { status: 400, statusText: 'Bad Request' });
  });

  // ✅ update()
  it('should update a session', () => {
    const updatedSession: Session = { id: 1, name: 'Updated', description: 'Desc', date: new Date(), teacher_id: 2, users: [] };
    service.update('1', updatedSession).subscribe(data => expect(data).toEqual(updatedSession));

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    req.flush(updatedSession);
  });

  it('should handle error on update()', () => {
    const updatedSession: Session = { id: 1, name: 'Updated', description: 'Desc', date: new Date(), teacher_id: 2, users: [] };
    service.update('1', updatedSession).subscribe({
      next: () => fail('should fail'),
      error: err => expect(err.status).toBe(404),
    });

    const req = httpMock.expectOne('api/session/1');
    req.flush({}, { status: 404, statusText: 'Not Found' });
  });

  // ✅ delete()
  it('should delete a session', () => {
    service.delete('1').subscribe(res => expect(res).toBeTruthy());

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('should handle error on delete()', () => {
    service.delete('1').subscribe({
      next: () => fail('should fail'),
      error: err => expect(err.status).toBe(404),
    });

    const req = httpMock.expectOne('api/session/1');
    req.flush({}, { status: 404, statusText: 'Not Found' });
  });

  // ✅ participate()
  it('should participate in a session', () => {
    service.participate('1', '10').subscribe(res => expect(res).toBeUndefined());

    const req = httpMock.expectOne('api/session/1/participate/10');
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  // ✅ unParticipate()
  it('should unParticipate in a session', () => {
    service.unParticipate('1', '10').subscribe(res => expect(res).toBeUndefined());

    const req = httpMock.expectOne('api/session/1/participate/10');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});