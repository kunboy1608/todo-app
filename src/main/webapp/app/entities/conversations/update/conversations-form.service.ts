import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IConversations, NewConversations } from '../conversations.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { conversationId: unknown }> = Partial<Omit<T, 'conversationId'>> & {
  conversationId: T['conversationId'];
};

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConversations for edit and NewConversationsFormGroupInput for create.
 */
type ConversationsFormGroupInput = IConversations | PartialWithRequiredKeyOf<NewConversations>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IConversations | NewConversations> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

type ConversationsFormRawValue = FormValueOf<IConversations>;

type NewConversationsFormRawValue = FormValueOf<NewConversations>;

type ConversationsFormDefaults = Pick<NewConversations, 'conversationId' | 'timestamp'>;

type ConversationsFormGroupContent = {
  conversationId: FormControl<ConversationsFormRawValue['conversationId'] | NewConversations['conversationId']>;
  timestamp: FormControl<ConversationsFormRawValue['timestamp']>;
  sender: FormControl<ConversationsFormRawValue['sender']>;
  receiver: FormControl<ConversationsFormRawValue['receiver']>;
  message: FormControl<ConversationsFormRawValue['message']>;
};

export type ConversationsFormGroup = FormGroup<ConversationsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConversationsFormService {
  createConversationsFormGroup(conversations: ConversationsFormGroupInput = { conversationId: null }): ConversationsFormGroup {
    const conversationsRawValue = this.convertConversationsToConversationsRawValue({
      ...this.getFormDefaults(),
      ...conversations,
    });
    return new FormGroup<ConversationsFormGroupContent>({
      conversationId: new FormControl(
        { value: conversationsRawValue.conversationId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      timestamp: new FormControl(conversationsRawValue.timestamp, {
        validators: [Validators.required],
      }),
      sender: new FormControl(conversationsRawValue.sender, {
        validators: [Validators.required],
      }),
      receiver: new FormControl(conversationsRawValue.receiver, {
        validators: [Validators.required],
      }),
      message: new FormControl(conversationsRawValue.message, {
        validators: [Validators.required],
      }),
    });
  }

  getConversations(form: ConversationsFormGroup): IConversations | NewConversations {
    return this.convertConversationsRawValueToConversations(form.getRawValue() as ConversationsFormRawValue | NewConversationsFormRawValue);
  }

  resetForm(form: ConversationsFormGroup, conversations: ConversationsFormGroupInput): void {
    const conversationsRawValue = this.convertConversationsToConversationsRawValue({ ...this.getFormDefaults(), ...conversations });
    form.reset(
      {
        ...conversationsRawValue,
        conversationId: { value: conversationsRawValue.conversationId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ConversationsFormDefaults {
    const currentTime = dayjs();

    return {
      conversationId: null,
      timestamp: currentTime,
    };
  }

  private convertConversationsRawValueToConversations(
    rawConversations: ConversationsFormRawValue | NewConversationsFormRawValue
  ): IConversations | NewConversations {
    return {
      ...rawConversations,
      timestamp: dayjs(rawConversations.timestamp, DATE_TIME_FORMAT),
    };
  }

  private convertConversationsToConversationsRawValue(
    conversations: IConversations | (Partial<NewConversations> & ConversationsFormDefaults)
  ): ConversationsFormRawValue | PartialWithRequiredKeyOf<NewConversationsFormRawValue> {
    return {
      ...conversations,
      timestamp: conversations.timestamp ? conversations.timestamp.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
