import dayjs from 'dayjs/esm';

import { IConversations, NewConversations } from './conversations.model';

export const sampleWithRequiredData: IConversations = {
  conversationId: 55209,
  timestamp: dayjs('2023-05-02T21:30'),
  sender: 45706,
  receiver: 29173,
  message: 'mobile Applications',
};

export const sampleWithPartialData: IConversations = {
  conversationId: 67533,
  timestamp: dayjs('2023-05-03T02:20'),
  sender: 79552,
  receiver: 31192,
  message: 'partnerships Shirt Kip',
};

export const sampleWithFullData: IConversations = {
  conversationId: 16716,
  timestamp: dayjs('2023-05-02T12:58'),
  sender: 27362,
  receiver: 51803,
  message: 'Trace',
};

export const sampleWithNewData: NewConversations = {
  timestamp: dayjs('2023-05-02T23:47'),
  sender: 46340,
  receiver: 27921,
  message: 'Money Accountability Lari',
  conversationId: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
