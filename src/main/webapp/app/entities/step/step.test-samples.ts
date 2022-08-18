import { IStep, NewStep } from './step.model';

export const sampleWithRequiredData: IStep = {
  id: '1dbd9897-8152-4070-a072-8b2c463908c2',
  name: 'SQL Idaho Rustic',
};

export const sampleWithPartialData: IStep = {
  id: '1f097a56-6500-47cc-abd9-f6e5ec28ce3a',
  name: 'copying',
};

export const sampleWithFullData: IStep = {
  id: '4ba95c5f-4e7e-41a4-91d3-c8222a2092c7',
  name: 'implementation Swaziland',
  note: 'synthesizing',
};

export const sampleWithNewData: NewStep = {
  name: 'Small up Ireland',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
