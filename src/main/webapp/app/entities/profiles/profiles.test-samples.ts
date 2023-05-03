import dayjs from 'dayjs/esm';

import { IProfiles, NewProfiles } from './profiles.model';

export const sampleWithRequiredData: IProfiles = {
  profileId: 40915,
};

export const sampleWithPartialData: IProfiles = {
  profileId: 51163,
  username: 'SMTP transparent',
  bio: 'grey archive',
  createdBy: 'Rubber',
  createdOn: dayjs('2023-05-03T02:00'),
  modifiedBy: 'Tactics',
};

export const sampleWithFullData: IProfiles = {
  profileId: 1333,
  username: 'invoice quantifying',
  nickname: 'Object-based hub',
  birthday: dayjs('2023-05-03'),
  bio: 'Bolivia',
  createdBy: 'Pizza',
  createdOn: dayjs('2023-05-02T21:02'),
  modifiedBy: 'Frozen',
  modifiedOn: dayjs('2023-05-03T03:08'),
};

export const sampleWithNewData: NewProfiles = {
  profileId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
