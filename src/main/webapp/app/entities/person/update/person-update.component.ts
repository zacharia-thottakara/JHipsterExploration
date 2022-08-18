import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PersonFormService, PersonFormGroup } from './person-form.service';
import { IPerson } from '../person.model';
import { PersonService } from '../service/person.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IStep } from 'app/entities/step/step.model';
import { StepService } from 'app/entities/step/service/step.service';

@Component({
  selector: 'jhi-person-update',
  templateUrl: './person-update.component.html',
})
export class PersonUpdateComponent implements OnInit {
  isSaving = false;
  person: IPerson | null = null;

  shares_withsCollection: IPerson[] = [];
  eventsSharedCollection: IEvent[] = [];
  stepsSharedCollection: IStep[] = [];

  editForm: PersonFormGroup = this.personFormService.createPersonFormGroup();

  constructor(
    protected personService: PersonService,
    protected personFormService: PersonFormService,
    protected eventService: EventService,
    protected stepService: StepService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  compareStep = (o1: IStep | null, o2: IStep | null): boolean => this.stepService.compareStep(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ person }) => {
      this.person = person;
      if (person) {
        this.updateForm(person);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const person = this.personFormService.getPerson(this.editForm);
    if (person.id !== null) {
      this.subscribeToSaveResponse(this.personService.update(person));
    } else {
      this.subscribeToSaveResponse(this.personService.create(person));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPerson>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(person: IPerson): void {
    this.person = person;
    this.personFormService.resetForm(this.editForm, person);

    this.shares_withsCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(
      this.shares_withsCollection,
      person.shares_with
    );
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(this.eventsSharedCollection, person.can_see);
    this.stepsSharedCollection = this.stepService.addStepToCollectionIfMissing<IStep>(this.stepsSharedCollection, person.can_do);
  }

  protected loadRelationshipsOptions(): void {
    this.personService
      .query({ filter: 'shares_with-is-null' })
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.person?.shares_with)))
      .subscribe((people: IPerson[]) => (this.shares_withsCollection = people));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.person?.can_see)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));

    this.stepService
      .query()
      .pipe(map((res: HttpResponse<IStep[]>) => res.body ?? []))
      .pipe(map((steps: IStep[]) => this.stepService.addStepToCollectionIfMissing<IStep>(steps, this.person?.can_do)))
      .subscribe((steps: IStep[]) => (this.stepsSharedCollection = steps));
  }
}
