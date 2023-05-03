import dayjs from 'dayjs/esm';

import { IConversationsDetails, NewConversationsDetails } from './conversations-details.model';

export const sampleWithRequiredData: IConversationsDetails = {
  id: 8718,
};

export const sampleWithPartialData: IConversationsDetails = {
  id: 47806,
  name: 'application discrete',
  isGroup: false,
  createdBy: 'invoice Grenada',
};

export const sampleWithFullData: IConversationsDetails = {
  id: 80033,
  name: 'Analyst Berkshire',
  isGroup: false,
  createdBy: 'Computer Strategist',
  createdOn: dayjs('2023-05-02T18:25'),
  modifiedBy: 'Berkshire SSL invoice',
  modifiedOn: dayjs('2023-05-02T13:09'),
};

export const sampleWithNewData: NewConversationsDetails = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
