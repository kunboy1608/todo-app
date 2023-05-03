import { IProfiles } from 'app/entities/profiles/profiles.model';

export interface INotes {
  noteId: number;
  owner?: number | null;
  content?: string | null;
  profiles?: Pick<IProfiles, 'profileId'> | null;
}

export type NewNotes = Omit<INotes, 'noteId'> & { noteId: null };
