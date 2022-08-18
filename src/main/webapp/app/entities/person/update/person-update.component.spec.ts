import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PersonFormService } from './person-form.service';
import { PersonService } from '../service/person.service';
import { IPerson } from '../person.model';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IStep } from 'app/entities/step/step.model';
import { StepService } from 'app/entities/step/service/step.service';

import { PersonUpdateComponent } from './person-update.component';

describe('Person Management Update Component', () => {
  let comp: PersonUpdateComponent;
  let fixture: ComponentFixture<PersonUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let personFormService: PersonFormService;
  let personService: PersonService;
  let eventService: EventService;
  let stepService: StepService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PersonUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PersonUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PersonUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    personFormService = TestBed.inject(PersonFormService);
    personService = TestBed.inject(PersonService);
    eventService = TestBed.inject(EventService);
    stepService = TestBed.inject(StepService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call shares_with query and add missing value', () => {
      const person: IPerson = { id: 'CBA' };
      const shares_with: IPerson = { id: 'b8d692dc-dd88-400a-8e13-3f4c3481ea6b' };
      person.shares_with = shares_with;

      const shares_withCollection: IPerson[] = [{ id: '1bf47592-0b49-4029-ba90-08c22502e778' }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: shares_withCollection })));
      const expectedCollection: IPerson[] = [shares_with, ...shares_withCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(shares_withCollection, shares_with);
      expect(comp.shares_withsCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const person: IPerson = { id: 'CBA' };
      const can_see: IEvent = { id: 'de4dafe0-5dde-4ab9-b16c-73fbf30fee08' };
      person.can_see = can_see;

      const eventCollection: IEvent[] = [{ id: '6089d33b-4ad3-42f3-beab-d6e620903151' }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [can_see];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining)
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Step query and add missing value', () => {
      const person: IPerson = { id: 'CBA' };
      const can_do: IStep = { id: 'f8a81a96-a04e-469b-9304-f7cd2767ee0b' };
      person.can_do = can_do;

      const stepCollection: IStep[] = [{ id: '80c50ac1-be55-4f8c-9537-4b5764037ecf' }];
      jest.spyOn(stepService, 'query').mockReturnValue(of(new HttpResponse({ body: stepCollection })));
      const additionalSteps = [can_do];
      const expectedCollection: IStep[] = [...additionalSteps, ...stepCollection];
      jest.spyOn(stepService, 'addStepToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(stepService.query).toHaveBeenCalled();
      expect(stepService.addStepToCollectionIfMissing).toHaveBeenCalledWith(
        stepCollection,
        ...additionalSteps.map(expect.objectContaining)
      );
      expect(comp.stepsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const person: IPerson = { id: 'CBA' };
      const shares_with: IPerson = { id: '73cb7147-4d28-4967-9da3-17270e73c561' };
      person.shares_with = shares_with;
      const can_see: IEvent = { id: '54c8e1ee-b0b2-422a-a682-ae66d8530222' };
      person.can_see = can_see;
      const can_do: IStep = { id: 'dcd528dd-eeef-42e2-8e36-47e5ff939cd5' };
      person.can_do = can_do;

      activatedRoute.data = of({ person });
      comp.ngOnInit();

      expect(comp.shares_withsCollection).toContain(shares_with);
      expect(comp.eventsSharedCollection).toContain(can_see);
      expect(comp.stepsSharedCollection).toContain(can_do);
      expect(comp.person).toEqual(person);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 'ABC' };
      jest.spyOn(personFormService, 'getPerson').mockReturnValue(person);
      jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: person }));
      saveSubject.complete();

      // THEN
      expect(personFormService.getPerson).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(personService.update).toHaveBeenCalledWith(expect.objectContaining(person));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 'ABC' };
      jest.spyOn(personFormService, 'getPerson').mockReturnValue({ id: null });
      jest.spyOn(personService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: person }));
      saveSubject.complete();

      // THEN
      expect(personFormService.getPerson).toHaveBeenCalled();
      expect(personService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPerson>>();
      const person = { id: 'ABC' };
      jest.spyOn(personService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ person });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(personService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePerson', () => {
      it('Should forward to personService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(personService, 'comparePerson');
        comp.comparePerson(entity, entity2);
        expect(personService.comparePerson).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEvent', () => {
      it('Should forward to eventService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(eventService, 'compareEvent');
        comp.compareEvent(entity, entity2);
        expect(eventService.compareEvent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStep', () => {
      it('Should forward to stepService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(stepService, 'compareStep');
        comp.compareStep(entity, entity2);
        expect(stepService.compareStep).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
