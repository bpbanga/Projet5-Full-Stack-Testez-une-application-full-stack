import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { expect } from '@jest/globals';
import { DetailComponent } from './detail.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from '../../../../interfaces/teacher.interface';
import { Router, ActivatedRoute } from '@angular/router';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let routerMock: any;
  let snackBarMock: any;

  const sessionMock: Session = {
    id: 1,
    name: 'Yoga',
    description: 'Morning Yoga',
    date: new Date(),
    createdAt: new Date(),
    updatedAt: new Date(),
    users: [1, 2],
    teacher_id: 1
  };

  const teacherMock: Teacher = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockSessionApiService = {
    detail: jest.fn().mockReturnValue(of(sessionMock)),
    delete: jest.fn().mockReturnValue(of(void 0)),
    participate: jest.fn().mockReturnValue(of(void 0)),
    unParticipate: jest.fn().mockReturnValue(of(void 0))
  };

  const mockTeacherService = {
    detail: jest.fn().mockReturnValue(of(teacherMock))
  };

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
      token: '',
      type: '',
      username: 'admin',
      firstName: 'Admin',
      lastName: 'User'
    }
  };

  const activatedRouteMock = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1') // Mock l'ID de la session
      }
    }
  };

  beforeEach(async () => {
    routerMock = { navigate: jest.fn() };
    snackBarMock = { open: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        FlexLayoutModule,
        BrowserAnimationsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: MatSnackBar, useValue: snackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock } // ✅ mock ajouté
      ],
      schemas: [NO_ERRORS_SCHEMA] // ignore les éléments Angular Material non importés
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // trigger ngOnInit
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display session information', () => {
    fixture.detectChanges();

    const title = fixture.debugElement.query(By.css('h1')).nativeElement.textContent;
    const description = fixture.debugElement.query(By.css('.description')).nativeElement.textContent;

    const attendeesSpan = fixture.debugElement.queryAll(By.css('.my2 div[fxLayoutAlign="start center"] span.ml1'))[0];
    const attendees = attendeesSpan.nativeElement.textContent;

    expect(title).toContain(sessionMock.name);
    expect(description).toContain(sessionMock.description);
    expect(attendees).toContain(sessionMock.users.length.toString());
  });

  it('should display Delete button if user is admin', () => {
    fixture.detectChanges();

    const deleteButton = fixture.debugElement.query(By.css('button[color="warn"]'));
    expect(deleteButton).toBeTruthy();
    expect(deleteButton.nativeElement.textContent).toContain('Delete');
  });

  it('should call delete on SessionApiService when delete button is clicked', () => {
    fixture.detectChanges();
    const deleteButton = fixture.debugElement.query(By.css('button[color="warn"]'));
    deleteButton.nativeElement.click();

expect(mockSessionApiService.delete).toHaveBeenCalledWith(sessionMock.id!.toString());
    expect(snackBarMock.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
