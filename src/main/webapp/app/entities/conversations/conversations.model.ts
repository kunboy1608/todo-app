import dayjs from 'dayjs/esm';

export interface IConversations {
  conversationId: number;
  timestamp?: dayjs.Dayjs | null;
  sender?: number | null;
  receiver?: number | null;
  message?: string | null;
}

export type NewConversations = Omit<IConversations, 'conversationId'> & { conversationId: null };
