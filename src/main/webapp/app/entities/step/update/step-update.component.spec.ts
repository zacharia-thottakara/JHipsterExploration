import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StepFormService } from './step-form.service';
import { StepService } from '../service/step.service';
import { IStep } from '../step.model';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

import { StepUpdateComponent } from './step-update.component';

describe('Step Management Update Component', () => {
  let comp: StepUpdateComponent;
  let fixture: ComponentFixture<StepUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stepFormService: StepFormService;
  let stepService: StepService;
  let eventService: EventService;
  let personService: PersonService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StepUpdateComponent],
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
      .overrideTemplate(StepUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StepUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stepFormService = TestBed.inject(StepFormService);
    stepService = TestBed.inject(StepService);
    eventService = TestBed.inject(EventService);
    personService = TestBed.inject(PersonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call do_next query and add missing value', () => {
      const step: IStep = { id: 'CBA' };
      const do_next: IStep = { id: '7f7b7f85-908a-49dd-971d-bd6c27b972c4' };
      step.do_next = do_next;

      const do_nextCollection: IStep[] = [{ id: '6270e1de-9ea9-4693-b015-97f8b0a5bc4a' }];
      jest.spyOn(stepService, 'query').mockReturnValue(of(new HttpResponse({ body: do_nextCollection })));
      const expectedCollection: IStep[] = [do_next, ...do_nextCollection];
      jest.spyOn(stepService, 'addStepToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ step });
      comp.ngOnInit();

      expect(stepService.query).toHaveBeenCalled();
      expect(stepService.addStepToCollectionIfMissing).toHaveBeenCalledWith(do_nextCollection, do_next);
      expect(comp.do_nextsCollection).toEqual(expectedCollection);
    });

    it('Should call dependent_on query and add missing value', () => {
      const step: IStep = { id: 'CBA' };
      const dependent_on: IStep = { id: '6a878a38-6805-4815-9185-f86056507629' };
      step.dependent_on = dependent_on;

      const dependent_onCollection: IStep[] = [{ id: '1b79e6ce-7117-4086-8a54-9f5ac7f28f47' }];
      jest.spyOn(stepService, 'query').mockReturnValue(of(new HttpResponse({ body: dependent_onCollection })));
      const expectedCollection: IStep[] = [dependent_on, ...dependent_onCollection];
      jest.spyOn(stepService, 'addStepToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ step });
      comp.ngOnInit();

      expect(stepService.query).toHaveBeenCalled();
      expect(stepService.addStepToCollectionIfMissing).toHaveBeenCalledWith(dependent_onCollection, dependent_on);
      expect(comp.dependent_onsCollection).toEqual(expectedCollection);
    });

    it('Should call Event query and add missing value', () => {
      const step: IStep = { id: 'CBA' };
      const action_of: IEvent = { id: 'd5c1d4bf-41c5-4bca-bca4-cbf6617d76bd' };
      step.action_of = action_of;

      const eventCollection: IEvent[] = [{ id: 'f02f1ba2-5d35-4f93-8fdb-58d78fb941b1' }];
      jest.spyOn(eventService, 'query').mockReturnValue(of(new HttpResponse({ body: eventCollection })));
      const additionalEvents = [action_of];
      const expectedCollection: IEvent[] = [...additionalEvents, ...eventCollection];
      jest.spyOn(eventService, 'addEventToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ step });
      comp.ngOnInit();

      expect(eventService.query).toHaveBeenCalled();
      expect(eventService.addEventToCollectionIfMissing).toHaveBeenCalledWith(
        eventCollection,
        ...additionalEvents.map(expect.objectContaining)
      );
      expect(comp.eventsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Person query and add missing value', () => {
      const step: IStep = { id: 'CBA' };
      const action_for: IPerson = { id: 'fa243859-8445-41b0-84e0-7cd20f1c4137' };
      step.action_for = action_for;

      const personCollection: IPerson[] = [{ id: 'd76f4383-df43-4579-837a-d5c50cc82723' }];
      jest.spyOn(personService, 'query').mockReturnValue(of(new HttpResponse({ body: personCollection })));
      const additionalPeople = [action_for];
      const expectedCollection: IPerson[] = [...additionalPeople, ...personCollection];
      jest.spyOn(personService, 'addPersonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ step });
      comp.ngOnInit();

      expect(personService.query).toHaveBeenCalled();
      expect(personService.addPersonToCollectionIfMissing).toHaveBeenCalledWith(
        personCollection,
        ...additionalPeople.map(expect.objectContaining)
      );
      expect(comp.peopleSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const step: IStep = { id: 'CBA' };
      const do_next: IStep = { id: '1343027a-4bc2-46a9-ad11-fb73a4d00a70' };
      step.do_next = do_next;
      const dependent_on: IStep = { id: 'a5c1caec-df9f-45c6-a40e-8c78730c40e7' };
      step.dependent_on = dependent_on;
      const action_of: IEvent = { id: '11bf445c-547a-4df5-b729-f4be0dcf55ef' };
      step.action_of = action_of;
      const action_for: IPerson = { id: '83808d24-db90-4365-b035-2ad526347c19' };
      step.action_for = action_for;

      activatedRoute.data = of({ step });
      comp.ngOnInit();

      expect(comp.do_nextsCollection).toContain(do_next);
      expect(comp.dependent_onsCollection).toContain(dependent_on);
      expect(comp.eventsSharedCollection).toContain(action_of);
      expect(comp.peopleSharedCollection).toContain(action_for);
      expect(comp.step).toEqual(step);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStep>>();
      const step = { id: 'ABC' };
      jest.spyOn(stepFormService, 'getStep').mockReturnValue(step);
      jest.spyOn(stepService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ step });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: step }));
      saveSubject.complete();

      // THEN
      expect(stepFormService.getStep).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(stepService.update).toHaveBeenCalledWith(expect.objectContaining(step));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStep>>();
      const step = { id: 'ABC' };
      jest.spyOn(stepFormService, 'getStep').mockReturnValue({ id: null });
      jest.spyOn(stepService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ step: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: step }));
      saveSubject.complete();

      // THEN
      expect(stepFormService.getStep).toHaveBeenCalled();
      expect(stepService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStep>>();
      const step = { id: 'ABC' };
      jest.spyOn(stepService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ step });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stepService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStep', () => {
      it('Should forward to stepService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(stepService, 'compareStep');
        comp.compareStep(entity, entity2);
        expect(stepService.compareStep).toHaveBeenCalledWith(entity, entity2);
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

    describe('comparePerson', () => {
      it('Should forward to personService', () => {
        const entity = { id: 'ABC' };
        const entity2 = { id: 'CBA' };
        jest.spyOn(personService, 'comparePerson');
        comp.comparePerson(entity, entity2);
        expect(personService.comparePerson).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
