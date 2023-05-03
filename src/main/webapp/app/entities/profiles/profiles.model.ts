import dayjs from 'dayjs/esm';

export interface IProfiles {
  profileId: number;
  username?: string | null;
  nickname?: string | null;
  birthday?: dayjs.Dayjs | null;
  bio?: string | null;
  createdBy?: string | null;
  createdOn?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  modifiedOn?: dayjs.Dayjs | null;
}

export type NewProfiles = Omit<IProfiles, 'profileId'> & { profileId: null };
