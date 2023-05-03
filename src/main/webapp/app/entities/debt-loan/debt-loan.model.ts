import dayjs from 'dayjs/esm';
import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface IDebtLoan {
  id: number;
  loanUserId?: number | null;
  debtUserId?: number | null;
  cost?: number | null;
  deadline?: dayjs.Dayjs | null;
  datOfPayment?: dayjs.Dayjs | null;
  debts?: Pick<IProfiles, 'profileId'> | null;
  loans?: Pick<IProfiles, 'profileId'> | null;
}

export type NewDebtLoan = Omit<IDebtLoan, 'id'> & { id: null };
