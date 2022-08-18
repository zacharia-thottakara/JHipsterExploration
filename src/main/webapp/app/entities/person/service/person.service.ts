import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPerson, NewPerson } from '../person.model';

export type PartialUpdatePerson = Partial<IPerson> & Pick<IPerson, 'id'>;

export type EntityResponseType = HttpResponse<IPerson>;
export type EntityArrayResponseType = HttpResponse<IPerson[]>;

@Injectable({ providedIn: 'root' })
export class PersonService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/people');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(person: NewPerson): Observable<EntityResponseType> {
    return this.http.post<IPerson>(this.resourceUrl, person, { observe: 'response' });
  }

  update(person: IPerson): Observable<EntityResponseType> {
    return this.http.put<IPerson>(`${this.resourceUrl}/${this.getPersonIdentifier(person)}`, person, { observe: 'response' });
  }

  partialUpdate(person: PartialUpdatePerson): Observable<EntityResponseType> {
    return this.http.patch<IPerson>(`${this.resourceUrl}/${this.getPersonIdentifier(person)}`, person, { observe: 'response' });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IPerson>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPerson[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPersonIdentifier(person: Pick<IPerson, 'id'>): string {
    return person.id;
  }

  comparePerson(o1: Pick<IPerson, 'id'> | null, o2: Pick<IPerson, 'id'> | null): boolean {
    return o1 && o2 ? this.getPersonIdentifier(o1) === this.getPersonIdentifier(o2) : o1 === o2;
  }

  addPersonToCollectionIfMissing<Type extends Pick<IPerson, 'id'>>(
    personCollection: Type[],
    ...peopleToCheck: (Type | null | undefined)[]
  ): Type[] {
    const people: Type[] = peopleToCheck.filter(isPresent);
    if (people.length > 0) {
      const personCollectionIdentifiers = personCollection.map(personItem => this.getPersonIdentifier(personItem)!);
      const peopleToAdd = people.filter(personItem => {
        const personIdentifier = this.getPersonIdentifier(personItem);
        if (personCollectionIdentifiers.includes(personIdentifier)) {
          return false;
        }
        personCollectionIdentifiers.push(personIdentifier);
        return true;
      });
      return [...peopleToAdd, ...personCollection];
    }
    return personCollection;
  }
}
