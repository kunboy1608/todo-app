import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface ITags {
  tagId: number;
  owner?: number | null;
  name?: string | null;
  profiles?: Pick<IProfiles, 'profileId'> | null;
}

export type NewTags = Omit<ITags, 'tagId'> & { tagId: null };
