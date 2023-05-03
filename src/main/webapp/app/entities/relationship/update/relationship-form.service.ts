import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRelationship, NewRelationship } from '../relationship.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { relationshipId: unknown }> = Partial<Omit<T, 'relationshipId'>> & {
  relationshipId: T['relationshipId'];
};

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRelationship for edit and NewRelationshipFormGroupInput for create.
 */
type RelationshipFormGroupInput = IRelationship | PartialWithRequiredKeyOf<NewRelationship>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRelationship | NewRelationship> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

type RelationshipFormRawValue = FormValueOf<IRelationship>;

type NewRelationshipFormRawValue = FormValueOf<NewRelationship>;

type RelationshipFormDefaults = Pick<NewRelationship, 'relationshipId' | 'createdOn' | 'modifiedOn'>;

type RelationshipFormGroupContent = {
  relationshipId: FormControl<RelationshipFormRawValue['relationshipId'] | NewRelationship['relationshipId']>;
  owner: FormControl<RelationshipFormRawValue['owner']>;
  partner: FormControl<RelationshipFormRawValue['partner']>;
  status: FormControl<RelationshipFormRawValue['status']>;
  createdBy: FormControl<RelationshipFormRawValue['createdBy']>;
  createdOn: FormControl<RelationshipFormRawValue['createdOn']>;
  modifiedBy: FormControl<RelationshipFormRawValue['modifiedBy']>;
  modifiedOn: FormControl<RelationshipFormRawValue['modifiedOn']>;
};

export type RelationshipFormGroup = FormGroup<RelationshipFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RelationshipFormService {
  createRelationshipFormGroup(relationship: RelationshipFormGroupInput = { relationshipId: null }): RelationshipFormGroup {
    const relationshipRawValue = this.convertRelationshipToRelationshipRawValue({
      ...this.getFormDefaults(),
      ...relationship,
    });
    return new FormGroup<RelationshipFormGroupContent>({
      relationshipId: new FormControl(
        { value: relationshipRawValue.relationshipId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      owner: new FormControl(relationshipRawValue.owner),
      partner: new FormControl(relationshipRawValue.partner),
      status: new FormControl(relationshipRawValue.status),
      createdBy: new FormControl(relationshipRawValue.createdBy),
      createdOn: new FormControl(relationshipRawValue.createdOn),
      modifiedBy: new FormControl(relationshipRawValue.modifiedBy),
      modifiedOn: new FormControl(relationshipRawValue.modifiedOn),
    });
  }

  getRelationship(form: RelationshipFormGroup): IRelationship | NewRelationship {
    return this.convertRelationshipRawValueToRelationship(form.getRawValue() as RelationshipFormRawValue | NewRelationshipFormRawValue);
  }

  resetForm(form: RelationshipFormGroup, relationship: RelationshipFormGroupInput): void {
    const relationshipRawValue = this.convertRelationshipToRelationshipRawValue({ ...this.getFormDefaults(), ...relationship });
    form.reset(
      {
        ...relationshipRawValue,
        relationshipId: { value: relationshipRawValue.relationshipId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): RelationshipFormDefaults {
    const currentTime = dayjs();

    return {
      relationshipId: null,
      createdOn: currentTime,
      modifiedOn: currentTime,
    };
  }

  private convertRelationshipRawValueToRelationship(
    rawRelationship: RelationshipFormRawValue | NewRelationshipFormRawValue
  ): IRelationship | NewRelationship {
    return {
      ...rawRelationship,
      createdOn: dayjs(rawRelationship.createdOn, DATE_TIME_FORMAT),
      modifiedOn: dayjs(rawRelationship.modifiedOn, DATE_TIME_FORMAT),
    };
  }

  private convertRelationshipToRelationshipRawValue(
    relationship: IRelationship | (Partial<NewRelationship> & RelationshipFormDefaults)
  ): RelationshipFormRawValue | PartialWithRequiredKeyOf<NewRelationshipFormRawValue> {
    return {
      ...relationship,
      createdOn: relationship.createdOn ? relationship.createdOn.format(DATE_TIME_FORMAT) : undefined,
      modifiedOn: relationship.modifiedOn ? relationship.modifiedOn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
