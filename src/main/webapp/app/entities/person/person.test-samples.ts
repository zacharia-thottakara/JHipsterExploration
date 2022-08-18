import { IPerson, NewPerson } from './person.model';

export const sampleWithRequiredData: IPerson = {
  id: '48b4a7bc-7213-4181-9656-aa525381b37f',
  name: 'Usability Borders Chicken',
  email: '_/@+@v:.37',
};

export const sampleWithPartialData: IPerson = {
  id: 'acd5e7b9-2a39-4e49-b5be-7b0e8638fc7d',
  name: 'metrics',
  email: ']@YIs@7<.>&*YJ',
  note: 'XML',
};

export const sampleWithFullData: IPerson = {
  id: '118738bc-f5c3-4e38-b3aa-7abe5d7a7310',
  name: 'compressing Handcrafted Auto',
  email: "')/xA]@WmNrQ.=^#>f)",
  note: 'Exclusive Pants attitude',
};

export const sampleWithNewData: NewPerson = {
  name: 'copying state California',
  email: 'kXA@BaE..9n',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
