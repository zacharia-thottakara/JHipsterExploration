import dayjs from 'dayjs/esm';
import { IPerson } from 'app/entities/person/person.model';

export interface IEvent {
  id: string;
  name?: string | null;
  initial?: dayjs.Dayjs | null;
  repeat?: string | null;
  note?: string | null;
  owned_by?: Pick<IPerson, 'id' | 'name'> | null;
}

export type NewEvent = Omit<IEvent, 'id'> & { id: null };
