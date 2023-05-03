import dayjs from 'dayjs/esm';
import { ITypes } from 'app/entities/types/types.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface IExpenses {
  expenseId: number;
  owner?: number | null;
  content?: string | null;
  cost?: number | null;
  tag?: string | null;
  day?: dayjs.Dayjs | null;
  createdBy?: string | null;
  createdOn?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  modifiedOn?: dayjs.Dayjs | null;
  types?: Pick<ITypes, 'typeId'> | null;
  profiles?: Pick<IProfiles, 'profileId'> | null;
}

export type NewExpenses = Omit<IExpenses, 'expenseId'> & { expenseId: null };
