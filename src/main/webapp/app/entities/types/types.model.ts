import dayjs from 'dayjs/esm';
import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface ITypes {
  typeId: number;
  name?: string | null;
  owner?: number | null;
  createdBy?: string | null;
  createdOn?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  modifiedOn?: dayjs.Dayjs | null;
  profiles?: Pick<IProfiles, 'profileId'> | null;
}

export type NewTypes = Omit<ITypes, 'typeId'> & { typeId: null };
