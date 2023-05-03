import { INotes, NewNotes } from './notes.model';

export const sampleWithRequiredData: INotes = {
  noteId: 15584,
};

export const sampleWithPartialData: INotes = {
  noteId: 96107,
  content: 'deposit indexing',
};

export const sampleWithFullData: INotes = {
  noteId: 74532,
  owner: 37918,
  content: 'out-of-the-box',
};

export const sampleWithNewData: NewNotes = {
  noteId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
