import dayjs from 'dayjs/esm';

import { IDebtLoan, NewDebtLoan } from './debt-loan.model';

export const sampleWithRequiredData: IDebtLoan = {
  id: 26965,
};

export const sampleWithPartialData: IDebtLoan = {
  id: 94409,
  loanUserId: 98363,
  debtUserId: 264,
};

export const sampleWithFullData: IDebtLoan = {
  id: 6291,
  loanUserId: 24307,
  debtUserId: 75543,
  cost: 54003,
  deadline: dayjs('2023-05-03T01:45'),
  datOfPayment: dayjs('2023-05-02T04:30'),
};

export const sampleWithNewData: NewDebtLoan = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
