import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IConversationsDetails, NewConversationsDetails } from '../conversations-details.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IConversationsDetails for edit and NewConversationsDetailsFormGroupInput for create.
 */
type ConversationsDetailsFormGroupInput = IConversationsDetails | PartialWithRequiredKeyOf<NewConversationsDetails>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IConversationsDetails | NewConversationsDetails> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

type ConversationsDetailsFormRawValue = FormValueOf<IConversationsDetails>;

type NewConversationsDetailsFormRawValue = FormValueOf<NewConversationsDetails>;

type ConversationsDetailsFormDefaults = Pick<NewConversationsDetails, 'id' | 'isGroup' | 'createdOn' | 'modifiedOn'>;

type ConversationsDetailsFormGroupContent = {
  id: FormControl<ConversationsDetailsFormRawValue['id'] | NewConversationsDetails['id']>;
  name: FormControl<ConversationsDetailsFormRawValue['name']>;
  isGroup: FormControl<ConversationsDetailsFormRawValue['isGroup']>;
  createdBy: FormControl<ConversationsDetailsFormRawValue['createdBy']>;
  createdOn: FormControl<ConversationsDetailsFormRawValue['createdOn']>;
  modifiedBy: FormControl<ConversationsDetailsFormRawValue['modifiedBy']>;
  modifiedOn: FormControl<ConversationsDetailsFormRawValue['modifiedOn']>;
  conversations: FormControl<ConversationsDetailsFormRawValue['conversations']>;
};

export type ConversationsDetailsFormGroup = FormGroup<ConversationsDetailsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ConversationsDetailsFormService {
  createConversationsDetailsFormGroup(
    conversationsDetails: ConversationsDetailsFormGroupInput = { id: null }
  ): ConversationsDetailsFormGroup {
    const conversationsDetailsRawValue = this.convertConversationsDetailsToConversationsDetailsRawValue({
      ...this.getFormDefaults(),
      ...conversationsDetails,
    });
    return new FormGroup<ConversationsDetailsFormGroupContent>({
      id: new FormControl(
        { value: conversationsDetailsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(conversationsDetailsRawValue.name),
      isGroup: new FormControl(conversationsDetailsRawValue.isGroup),
      createdBy: new FormControl(conversationsDetailsRawValue.createdBy),
      createdOn: new FormControl(conversationsDetailsRawValue.createdOn),
      modifiedBy: new FormControl(conversationsDetailsRawValue.modifiedBy),
      modifiedOn: new FormControl(conversationsDetailsRawValue.modifiedOn),
      conversations: new FormControl(conversationsDetailsRawValue.conversations),
    });
  }

  getConversationsDetails(form: ConversationsDetailsFormGroup): IConversationsDetails | NewConversationsDetails {
    return this.convertConversationsDetailsRawValueToConversationsDetails(
      form.getRawValue() as ConversationsDetailsFormRawValue | NewConversationsDetailsFormRawValue
    );
  }

  resetForm(form: ConversationsDetailsFormGroup, conversationsDetails: ConversationsDetailsFormGroupInput): void {
    const conversationsDetailsRawValue = this.convertConversationsDetailsToConversationsDetailsRawValue({
      ...this.getFormDefaults(),
      ...conversationsDetails,
    });
    form.reset(
      {
        ...conversationsDetailsRawValue,
        id: { value: conversationsDetailsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ConversationsDetailsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isGroup: false,
      createdOn: currentTime,
      modifiedOn: currentTime,
    };
  }

  private convertConversationsDetailsRawValueToConversationsDetails(
    rawConversationsDetails: ConversationsDetailsFormRawValue | NewConversationsDetailsFormRawValue
  ): IConversationsDetails | NewConversationsDetails {
    return {
      ...rawConversationsDetails,
      createdOn: dayjs(rawConversationsDetails.createdOn, DATE_TIME_FORMAT),
      modifiedOn: dayjs(rawConversationsDetails.modifiedOn, DATE_TIME_FORMAT),
    };
  }

  private convertConversationsDetailsToConversationsDetailsRawValue(
    conversationsDetails: IConversationsDetails | (Partial<NewConversationsDetails> & ConversationsDetailsFormDefaults)
  ): ConversationsDetailsFormRawValue | PartialWithRequiredKeyOf<NewConversationsDetailsFormRawValue> {
    return {
      ...conversationsDetails,
      createdOn: conversationsDetails.createdOn ? conversationsDetails.createdOn.format(DATE_TIME_FORMAT) : undefined,
      modifiedOn: conversationsDetails.modifiedOn ? conversationsDetails.modifiedOn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
