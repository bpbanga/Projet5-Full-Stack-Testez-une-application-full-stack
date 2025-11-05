/// <reference types="cypress" />

const adminEmail = 'yoga@studio.com';
const adminPassword = 'test!1234';
const sessionName = 'Cypress Yoga Session';

describe('Create Session Flow', () => {
  beforeEach(() => {
    // Login avant chaque test
    cy.visit('/login');
    cy.get('input[formcontrolname="email"]').type(adminEmail);
    cy.get('input[formcontrolname="password"]').type(adminPassword);
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/sessions');
  });

  it('should create a new session successfully', () => {
    cy.contains('Create').click();

    cy.get('input[formcontrolname="name"]').type(sessionName);

    const date = new Date();
    const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2,'0')}-${String(date.getDate()).padStart(2,'0')}`;
    cy.get('input[formcontrolname="date"]').type(formattedDate);

    cy.get('mat-select[formcontrolname="teacher_id"]').click();
    cy.get('mat-option').first().click();

    cy.get('textarea[formcontrolname="description"]').type('This is a test session created with Cypress.');

    cy.get('button[type="submit"]').should('not.be.disabled').click();

    cy.url().should('include', '/sessions');
    cy.contains('Session created !').should('exist');
  });
});

describe('Session Detail & Update Flow', () => {
  const sessionName = 'Cypress Yoga Session';

  beforeEach(() => {
    // Login à chaque test
    cy.visit('/login');
    cy.get('input[formcontrolname="email"]').type('yoga@studio.com');
    cy.get('input[formcontrolname="password"]').type('test!1234');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/sessions');
    cy.wait(1000); 
  });

  it('should open session details', () => {
    cy.contains('mat-card', sessionName).contains('Detail').click();
    cy.url().should('include', '/sessions/detail');
    cy.get('img[alt="Yoga session"]').should('be.visible');
    cy.contains('button', 'Delete').should('exist');
    cy.get('mat-card-subtitle').within(() => {
      cy.get('mat-icon').should('contain.text', 'people');
      cy.get('span').invoke('text').should('not.be.empty');
    });
    cy.get('mat-card-content div').first().within(() => {
      cy.get('div').first().within(() => {
        cy.get('mat-icon').should('contain.text', 'group');
        cy.get('span').invoke('text').should('contain', 'attendees');
      });
      cy.get('div').eq(1).within(() => {
        cy.get('mat-icon').should('contain.text', 'calendar_month');
        cy.get('span').invoke('text').should('not.be.empty');
      });
    });
    cy.get('div.description').should('contain.text', 'Description:');
    cy.get('div.created').should('contain.text', 'Create at:');
    cy.get('div.updated').should('contain.text', 'Last update:');
  });

  it('should update session description', () => {
    cy.contains('mat-card', sessionName).contains('Edit').click();
    cy.url().should('include', '/sessions/update');
    cy.get('input[formcontrolname="name"]').should('have.value', sessionName);
    cy.get('textarea[formcontrolname="description"]')
      .clear()
      .type('Updated description with Cypress');
    cy.get('button[type="submit"]').should('not.be.disabled').click();
    cy.url().should('include', '/sessions');
    cy.contains('Session updated !').should('exist');
  });
});

describe('Delete Session Flow', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.get('input[formcontrolname="email"]').type(adminEmail);
    cy.get('input[formcontrolname="password"]').type(adminPassword);
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/sessions');
  });

  it('should delete session', () => {
    cy.contains('mat-card', sessionName).contains('Detail').click();
    cy.url().should('include', '/sessions/detail');

    cy.contains('button', 'Delete').click();
    cy.url().should('include', '/sessions');
    cy.contains('Session deleted !').should('be.visible');
  });
});
