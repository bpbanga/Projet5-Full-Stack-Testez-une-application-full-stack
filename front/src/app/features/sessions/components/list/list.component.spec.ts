import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { ListComponent } from './list.component';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';


describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let mockSessionApiService: Partial<SessionApiService>;
  let mockSessionService: Partial<SessionService>;

  const sessionsMock = [
    { id: 1, name: 'Yoga', description: 'Morning Yoga', date: new Date() },
    { id: 2, name: 'Pilates', description: 'Evening Pilates', date: new Date() }
  ];

  beforeEach(async () => {
    mockSessionApiService = {
      all: jest.fn().mockReturnValue(of(sessionsMock))
    };

    mockSessionService = {
      sessionInformation: { admin: true, token: '', type: '', id: 1, username: 'admin', firstName: 'Admin', lastName: 'User' }
    };

    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [MatCardModule, MatIconModule,RouterTestingModule,],
      providers: [
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ✅ Test : affichage de la liste des sessions
  it('should display a list of sessions', () => {
    // Rafraîchir le DOM après l'observable
    fixture.detectChanges();

    const sessionCards = fixture.debugElement.queryAll(By.css('.item'));
    expect(sessionCards.length).toBe(sessionsMock.length);

    // Vérifie que chaque session a le bon nom et description
    sessionsMock.forEach((session, index) => {
      const title = sessionCards[index].query(By.css('mat-card-title')).nativeElement.textContent;
      const description = sessionCards[index].query(By.css('mat-card-content p')).nativeElement.textContent;
      expect(title).toContain(session.name);
      expect(description).toContain(session.description);
    });
  });

   // ✅ Test : apparition des boutons Create et Detail pour un admin
  it('should show Create button and Detail buttons if user is admin', () => {
    fixture.detectChanges();

    // Bouton "Create" en haut
    const createButton = fixture.debugElement.query(By.css('[data-testid="create-btn"]'));
    expect(createButton).toBeTruthy();

    // Boutons "Detail" pour chaque session
    const detailButtons = fixture.debugElement.queryAll(By.css('[data-testid="detail-btn"]'));
    expect(detailButtons.length).toBe(sessionsMock.length);

    // Vérifie que chaque bouton "Detail" contient un icône "search"
    detailButtons.forEach(btn => {
      const icon = btn.query(By.css('mat-icon')).nativeElement.textContent;
      expect(icon).toContain('search');
    });
  });

  // ✅ Test optionnel : vérifier que le bouton Edit apparaît pour les admins
  it('should show Edit buttons for each session if user is admin', () => {
    fixture.detectChanges();
    const editButtons = fixture.debugElement.queryAll(By.css('[data-testid="edit-btn"]'));
    expect(editButtons.length).toBe(sessionsMock.length);
  });
});
