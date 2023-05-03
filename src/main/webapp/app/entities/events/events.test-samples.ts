import { IEvents, NewEvents } from './events.model';

export const sampleWithRequiredData: IEvents = {
  eventId: 89576,
};

export const sampleWithPartialData: IEvents = {
  eventId: 59412,
  kind: 44653,
};

export const sampleWithFullData: IEvents = {
  eventId: 57347,
  owner: 35131,
  kind: 52441,
  date: 'capacitor Cotton magenta',
  isLunar: true,
};

export const sampleWithNewData: NewEvents = {
  eventId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
