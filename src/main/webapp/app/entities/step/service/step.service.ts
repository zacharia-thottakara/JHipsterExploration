import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStep, NewStep } from '../step.model';

export type PartialUpdateStep = Partial<IStep> & Pick<IStep, 'id'>;

export type EntityResponseType = HttpResponse<IStep>;
export type EntityArrayResponseType = HttpResponse<IStep[]>;

@Injectable({ providedIn: 'root' })
export class StepService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/steps');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(step: NewStep): Observable<EntityResponseType> {
    return this.http.post<IStep>(this.resourceUrl, step, { observe: 'response' });
  }

  update(step: IStep): Observable<EntityResponseType> {
    return this.http.put<IStep>(`${this.resourceUrl}/${this.getStepIdentifier(step)}`, step, { observe: 'response' });
  }

  partialUpdate(step: PartialUpdateStep): Observable<EntityResponseType> {
    return this.http.patch<IStep>(`${this.resourceUrl}/${this.getStepIdentifier(step)}`, step, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IStep>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStep[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStepIdentifier(step: Pick<IStep, 'id'>): string {
    return step.id;
  }

  compareStep(o1: Pick<IStep, 'id'> | null, o2: Pick<IStep, 'id'> | null): boolean {
    return o1 && o2 ? this.getStepIdentifier(o1) === this.getStepIdentifier(o2) : o1 === o2;
  }

  addStepToCollectionIfMissing<Type extends Pick<IStep, 'id'>>(
    stepCollection: Type[],
    ...stepsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const steps: Type[] = stepsToCheck.filter(isPresent);
    if (steps.length > 0) {
      const stepCollectionIdentifiers = stepCollection.map(stepItem => this.getStepIdentifier(stepItem)!);
      const stepsToAdd = steps.filter(stepItem => {
        const stepIdentifier = this.getStepIdentifier(stepItem);
        if (stepCollectionIdentifiers.includes(stepIdentifier)) {
          return false;
        }
        stepCollectionIdentifiers.push(stepIdentifier);
        return true;
      });
      return [...stepsToAdd, ...stepCollection];
    }
    return stepCollection;
  }
}
