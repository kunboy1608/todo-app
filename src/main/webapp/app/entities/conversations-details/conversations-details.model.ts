import dayjs from 'dayjs/esm';
import { IConversations } from 'app/entities/conversations/conversations.model';

export interface IConversationsDetails {
  id: number;
  name?: string | null;
  isGroup?: boolean | null;
  createdBy?: string | null;
  createdOn?: dayjs.Dayjs | null;
  modifiedBy?: string | null;
  modifiedOn?: dayjs.Dayjs | null;
  conversations?: Pick<IConversations, 'conversationId'> | null;
}

export type NewConversationsDetails = Omit<IConversationsDetails, 'id'> & { id: null };
