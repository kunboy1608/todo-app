import { ITags, NewTags } from './tags.model';

export const sampleWithRequiredData: ITags = {
  tagId: 54200,
};

export const sampleWithPartialData: ITags = {
  tagId: 81990,
  name: 'Tuna Practical platforms',
};

export const sampleWithFullData: ITags = {
  tagId: 18382,
  owner: 9260,
  name: 'bluetooth generate Designer',
};

export const sampleWithNewData: NewTags = {
  tagId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
