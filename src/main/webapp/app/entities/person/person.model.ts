import { IEvent } from 'app/entities/event/event.model';
import { IStep } from 'app/entities/step/step.model';

export interface IPerson {
  id: string;
  name?: string | null;
  email?: string | null;
  note?: string | null;
  shares_with?: Pick<IPerson, 'id' | 'name'> | null;
  can_do?: Pick<IStep, 'id' | 'name'> | null;
  can_see?: Pick<IEvent, 'id' | 'name'> | null;
}

export type NewPerson = Omit<IPerson, 'id'> & { id: null };
