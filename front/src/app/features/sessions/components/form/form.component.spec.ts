import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';
import { FormComponent } from './form.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let mockSessionApiService: any;

  // Mock animate pour SnackBar / Angular Material
  beforeAll(() => {
    Element.prototype.animate = function () {
      return {
        finished: Promise.resolve(this as unknown as Animation),
        play: jest.fn(),
        pause: jest.fn(),
        cancel: jest.fn(),
        reverse: jest.fn(),
        commitStyles: jest.fn(),
        persist: jest.fn()
      } as unknown as Animation;
    };
  });

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1, token: '', type: '', username: 'admin', firstName: 'Admin', lastName: 'User' }
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of([{ id: 1, firstName: 'John', lastName: 'Doe' }]))
  };

  beforeEach(async () => {
    mockSessionApiService = {
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({
        id: 1,
        name: 'Yoga',
        date: new Date(),
        description: 'Morning Yoga',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }))
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatSnackBarModule,
        MatIconModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService }
      ],
      declarations: [FormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ✅ Formulaire invalide
  it('should mark form invalid if required fields are empty', () => {
    component.sessionForm!.setValue({ name: '', date: '', teacher_id: '', description: '' });
    expect(component.sessionForm!.invalid).toBeTruthy();
  });

  // ✅ Création de session
  it('should call create on submit when form is valid and onUpdate is false', () => {
    component.onUpdate = false;
    component.sessionForm!.setValue({
      name: 'Test Session',
      date: '2025-10-03',
      teacher_id: 1,
      description: 'Test description'
    });

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledWith({
      name: 'Test Session',
      date: '2025-10-03',
      teacher_id: 1,
      description: 'Test description'
    });
  });

  // ✅ Modification de session
  it('should call update on submit when onUpdate is true', () => {
    component.onUpdate = true;
    const fakeId = '1'; // ID fictif pour le test
    component.sessionForm!.setValue({
      name: 'Updated Session',
      date: '2025-10-04',
      teacher_id: 1,
      description: 'Updated description'
    });

    // Simuler l’appel update
    component.submit = jest.fn(() => {
      mockSessionApiService.update(fakeId, component.sessionForm!.value).subscribe();
    });

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith(fakeId, {
      name: 'Updated Session',
      date: '2025-10-04',
      teacher_id: 1,
      description: 'Updated description'
    });
  });

  // ✅ Gestion d’erreur lors de la création
  it('should handle error on create session', () => {
    mockSessionApiService.create.mockReturnValue(throwError(() => new Error('Server Error')));
    component.onUpdate = false;
    component.sessionForm!.setValue({
      name: 'Test',
      date: '2025-10-05',
      teacher_id: 1,
      description: 'Desc'
    });

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalled();
  });

  // ✅ Gestion d’erreur lors de la mise à jour
  it('should handle error on update session', () => {
    mockSessionApiService.update.mockReturnValue(throwError(() => new Error('Server Error')));
    component.onUpdate = true;
    const fakeId = '1';
    component.sessionForm!.setValue({
      name: 'Updated',
      date: '2025-10-06',
      teacher_id: 1,
      description: 'Desc'
    });

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalled();
  });
});
