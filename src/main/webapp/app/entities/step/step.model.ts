import { IEvent } from 'app/entities/event/event.model';
import { IPerson } from 'app/entities/person/person.model';

export interface IStep {
  id: string;
  name?: string | null;
  note?: string | null;
  do_next?: Pick<IStep, 'id' | 'name'> | null;
  dependent_on?: Pick<IStep, 'id' | 'name'> | null;
  action_of?: Pick<IEvent, 'id' | 'name'> | null;
  action_for?: Pick<IPerson, 'id' | 'name'> | null;
}

export type NewStep = Omit<IStep, 'id'> & { id: null };
