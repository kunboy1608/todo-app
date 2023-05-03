import dayjs from 'dayjs/esm';

export interface IRelationship {
  relationshipId: number;
  owner?: number | null;
  partner?: number | null;
  status?: number | null;
  createdBy?: string | null;
  createdOn?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  modifiedOn?: dayjs.Dayjs | null;
}

export type NewRelationship = Omit<IRelationship, 'relationshipId'> & { relationshipId: null };
