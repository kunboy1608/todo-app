import dayjs from 'dayjs/esm';

import { ITypes, NewTypes } from './types.model';

export const sampleWithRequiredData: ITypes = {
  typeId: 73468,
};

export const sampleWithPartialData: ITypes = {
  typeId: 51364,
  modifiedBy: 'pixel',
};

export const sampleWithFullData: ITypes = {
  typeId: 54963,
  name: 'Dobra Customer Wyoming',
  owner: 31122,
  createdBy: 'Mississippi Technician',
  createdOn: dayjs('2023-05-02T16:52'),
  modifiedBy: 'Nepalese Garden',
  modifiedOn: dayjs('2023-05-02T18:44'),
};

export const sampleWithNewData: NewTypes = {
  typeId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
