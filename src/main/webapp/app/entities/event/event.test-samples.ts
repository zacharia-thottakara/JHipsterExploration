import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 'a31c6a78-2023-404b-961a-533ca290ed9f',
  name: 'deposit',
  initial: dayjs('2022-08-18'),
};

export const sampleWithPartialData: IEvent = {
  id: 'c4bdbda4-0d2d-4c0b-8a53-2677cbb71d75',
  name: 'payment Optimization Sleek',
  initial: dayjs('2022-08-17'),
  repeat: 'Future-proofed',
};

export const sampleWithFullData: IEvent = {
  id: 'd7d73ff9-50b2-4a41-a9c3-8ccb048ec372',
  name: 'Designer',
  initial: dayjs('2022-08-18'),
  repeat: 'connecting',
  note: 'tangible Architect',
};

export const sampleWithNewData: NewEvent = {
  name: 'Danish time-frame Pizza',
  initial: dayjs('2022-08-18'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
