import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface IEvents {
  eventId: number;
  owner?: number | null;
  kind?: number | null;
  date?: string | null;
  isLunar?: boolean | null;
  profiles?: Pick<IProfiles, 'profileId'> | null;
}

export type NewEvents = Omit<IEvents, 'eventId'> & { eventId: null };
