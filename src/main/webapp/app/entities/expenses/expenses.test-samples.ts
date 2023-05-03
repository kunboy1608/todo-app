import dayjs from 'dayjs/esm';

import { IExpenses, NewExpenses } from './expenses.model';

export const sampleWithRequiredData: IExpenses = {
  expenseId: 77490,
};

export const sampleWithPartialData: IExpenses = {
  expenseId: 81113,
  tag: 'Grocery',
  createdBy: 'Samoa one-to-one',
  createdOn: dayjs('2023-05-02T10:01'),
};

export const sampleWithFullData: IExpenses = {
  expenseId: 35369,
  owner: 16057,
  content: 'Cambridgeshire syndicate Licensed',
  cost: 16194,
  tag: 'EXE payment concept',
  day: dayjs('2023-05-02'),
  createdBy: 'Facilitator',
  createdOn: dayjs('2023-05-02T05:13'),
  modifiedBy: 'Fantastic Unbranded exploit',
  modifiedOn: dayjs('2023-05-03T00:53'),
};

export const sampleWithNewData: NewExpenses = {
  expenseId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
