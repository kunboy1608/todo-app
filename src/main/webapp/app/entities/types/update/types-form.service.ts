import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITypes, NewTypes } from '../types.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { typeId: unknown }> = Partial<Omit<T, 'typeId'>> & { typeId: T['typeId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITypes for edit and NewTypesFormGroupInput for create.
 */
type TypesFormGroupInput = ITypes | PartialWithRequiredKeyOf<NewTypes>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITypes | NewTypes> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

type TypesFormRawValue = FormValueOf<ITypes>;

type NewTypesFormRawValue = FormValueOf<NewTypes>;

type TypesFormDefaults = Pick<NewTypes, 'typeId' | 'createdOn' | 'modifiedOn'>;

type TypesFormGroupContent = {
  typeId: FormControl<TypesFormRawValue['typeId'] | NewTypes['typeId']>;
  name: FormControl<TypesFormRawValue['name']>;
  owner: FormControl<TypesFormRawValue['owner']>;
  createdBy: FormControl<TypesFormRawValue['createdBy']>;
  createdOn: FormControl<TypesFormRawValue['createdOn']>;
  modifiedBy: FormControl<TypesFormRawValue['modifiedBy']>;
  modifiedOn: FormControl<TypesFormRawValue['modifiedOn']>;
  profiles: FormControl<TypesFormRawValue['profiles']>;
};

export type TypesFormGroup = FormGroup<TypesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TypesFormService {
  createTypesFormGroup(types: TypesFormGroupInput = { typeId: null }): TypesFormGroup {
    const typesRawValue = this.convertTypesToTypesRawValue({
      ...this.getFormDefaults(),
      ...types,
    });
    return new FormGroup<TypesFormGroupContent>({
      typeId: new FormControl(
        { value: typesRawValue.typeId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(typesRawValue.name),
      owner: new FormControl(typesRawValue.owner),
      createdBy: new FormControl(typesRawValue.createdBy),
      createdOn: new FormControl(typesRawValue.createdOn),
      modifiedBy: new FormControl(typesRawValue.modifiedBy),
      modifiedOn: new FormControl(typesRawValue.modifiedOn),
      profiles: new FormControl(typesRawValue.profiles),
    });
  }

  getTypes(form: TypesFormGroup): ITypes | NewTypes {
    return this.convertTypesRawValueToTypes(form.getRawValue() as TypesFormRawValue | NewTypesFormRawValue);
  }

  resetForm(form: TypesFormGroup, types: TypesFormGroupInput): void {
    const typesRawValue = this.convertTypesToTypesRawValue({ ...this.getFormDefaults(), ...types });
    form.reset(
      {
        ...typesRawValue,
        typeId: { value: typesRawValue.typeId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TypesFormDefaults {
    const currentTime = dayjs();

    return {
      typeId: null,
      createdOn: currentTime,
      modifiedOn: currentTime,
    };
  }

  private convertTypesRawValueToTypes(rawTypes: TypesFormRawValue | NewTypesFormRawValue): ITypes | NewTypes {
    return {
      ...rawTypes,
      createdOn: dayjs(rawTypes.createdOn, DATE_TIME_FORMAT),
      modifiedOn: dayjs(rawTypes.modifiedOn, DATE_TIME_FORMAT),
    };
  }

  private convertTypesToTypesRawValue(
    types: ITypes | (Partial<NewTypes> & TypesFormDefaults)
  ): TypesFormRawValue | PartialWithRequiredKeyOf<NewTypesFormRawValue> {
    return {
      ...types,
      createdOn: types.createdOn ? types.createdOn.format(DATE_TIME_FORMAT) : undefined,
      modifiedOn: types.modifiedOn ? types.modifiedOn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
