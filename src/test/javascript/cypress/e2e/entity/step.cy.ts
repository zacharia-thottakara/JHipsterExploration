import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Step e2e test', () => {
  const stepPageUrl = '/step';
  const stepPageUrlPattern = new RegExp('/step(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stepSample = { name: 'Movies' };

  let step;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/steps+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/steps').as('postEntityRequest');
    cy.intercept('DELETE', '/api/steps/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (step) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/steps/${step.id}`,
      }).then(() => {
        step = undefined;
      });
    }
  });

  it('Steps menu should load Steps page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('step');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Step').should('exist');
    cy.url().should('match', stepPageUrlPattern);
  });

  describe('Step page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stepPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Step page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/step/new$'));
        cy.getEntityCreateUpdateHeading('Step');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stepPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/steps',
          body: stepSample,
        }).then(({ body }) => {
          step = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/steps+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/steps?page=0&size=20>; rel="last",<http://localhost/api/steps?page=0&size=20>; rel="first"',
              },
              body: [step],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(stepPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Step page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('step');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stepPageUrlPattern);
      });

      it('edit button click should load edit Step page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Step');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stepPageUrlPattern);
      });

      it('edit button click should load edit Step page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Step');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stepPageUrlPattern);
      });

      it('last delete button click should delete instance of Step', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('step').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stepPageUrlPattern);

        step = undefined;
      });
    });
  });

  describe('new Step page', () => {
    beforeEach(() => {
      cy.visit(`${stepPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Step');
    });

    it('should create an instance of Step', () => {
      cy.get(`[data-cy="name"]`).type('Program Centralized').should('have.value', 'Program Centralized');

      cy.get(`[data-cy="note"]`).type('Lempira').should('have.value', 'Lempira');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        step = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', stepPageUrlPattern);
    });
  });
});
