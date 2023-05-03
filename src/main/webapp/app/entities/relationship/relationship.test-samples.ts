import dayjs from 'dayjs/esm';

import { IRelationship, NewRelationship } from './relationship.model';

export const sampleWithRequiredData: IRelationship = {
  relationshipId: 63972,
};

export const sampleWithPartialData: IRelationship = {
  relationshipId: 42294,
  owner: 38245,
  status: 82694,
  createdOn: dayjs('2023-05-02T10:33'),
};

export const sampleWithFullData: IRelationship = {
  relationshipId: 1788,
  owner: 72874,
  partner: 45374,
  status: 1448,
  createdBy: 'drive',
  createdOn: dayjs('2023-05-02T10:51'),
  modifiedBy: 'hack Configuration Berkshire',
  modifiedOn: dayjs('2023-05-02T16:41'),
};

export const sampleWithNewData: NewRelationship = {
  relationshipId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
