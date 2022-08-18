import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { StepFormService, StepFormGroup } from './step-form.service';
import { IStep } from '../step.model';
import { StepService } from '../service/step.service';
import { IEvent } from 'app/entities/event/event.model';
import { EventService } from 'app/entities/event/service/event.service';
import { IPerson } from 'app/entities/person/person.model';
import { PersonService } from 'app/entities/person/service/person.service';

@Component({
  selector: 'jhi-step-update',
  templateUrl: './step-update.component.html',
})
export class StepUpdateComponent implements OnInit {
  isSaving = false;
  step: IStep | null = null;

  do_nextsCollection: IStep[] = [];
  dependent_onsCollection: IStep[] = [];
  eventsSharedCollection: IEvent[] = [];
  peopleSharedCollection: IPerson[] = [];

  editForm: StepFormGroup = this.stepFormService.createStepFormGroup();

  constructor(
    protected stepService: StepService,
    protected stepFormService: StepFormService,
    protected eventService: EventService,
    protected personService: PersonService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareStep = (o1: IStep | null, o2: IStep | null): boolean => this.stepService.compareStep(o1, o2);

  compareEvent = (o1: IEvent | null, o2: IEvent | null): boolean => this.eventService.compareEvent(o1, o2);

  comparePerson = (o1: IPerson | null, o2: IPerson | null): boolean => this.personService.comparePerson(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ step }) => {
      this.step = step;
      if (step) {
        this.updateForm(step);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const step = this.stepFormService.getStep(this.editForm);
    if (step.id !== null) {
      this.subscribeToSaveResponse(this.stepService.update(step));
    } else {
      this.subscribeToSaveResponse(this.stepService.create(step));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStep>>): void {
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

  protected updateForm(step: IStep): void {
    this.step = step;
    this.stepFormService.resetForm(this.editForm, step);

    this.do_nextsCollection = this.stepService.addStepToCollectionIfMissing<IStep>(this.do_nextsCollection, step.do_next);
    this.dependent_onsCollection = this.stepService.addStepToCollectionIfMissing<IStep>(this.dependent_onsCollection, step.dependent_on);
    this.eventsSharedCollection = this.eventService.addEventToCollectionIfMissing<IEvent>(this.eventsSharedCollection, step.action_of);
    this.peopleSharedCollection = this.personService.addPersonToCollectionIfMissing<IPerson>(this.peopleSharedCollection, step.action_for);
  }

  protected loadRelationshipsOptions(): void {
    this.stepService
      .query({ filter: 'dependent_on-is-null' })
      .pipe(map((res: HttpResponse<IStep[]>) => res.body ?? []))
      .pipe(map((steps: IStep[]) => this.stepService.addStepToCollectionIfMissing<IStep>(steps, this.step?.do_next)))
      .subscribe((steps: IStep[]) => (this.do_nextsCollection = steps));

    this.stepService
      .query({ filter: 'do_next-is-null' })
      .pipe(map((res: HttpResponse<IStep[]>) => res.body ?? []))
      .pipe(map((steps: IStep[]) => this.stepService.addStepToCollectionIfMissing<IStep>(steps, this.step?.dependent_on)))
      .subscribe((steps: IStep[]) => (this.dependent_onsCollection = steps));

    this.eventService
      .query()
      .pipe(map((res: HttpResponse<IEvent[]>) => res.body ?? []))
      .pipe(map((events: IEvent[]) => this.eventService.addEventToCollectionIfMissing<IEvent>(events, this.step?.action_of)))
      .subscribe((events: IEvent[]) => (this.eventsSharedCollection = events));

    this.personService
      .query()
      .pipe(map((res: HttpResponse<IPerson[]>) => res.body ?? []))
      .pipe(map((people: IPerson[]) => this.personService.addPersonToCollectionIfMissing<IPerson>(people, this.step?.action_for)))
      .subscribe((people: IPerson[]) => (this.peopleSharedCollection = people));
  }
}
