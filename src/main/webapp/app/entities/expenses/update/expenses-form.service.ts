import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IExpenses, NewExpenses } from '../expenses.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { expenseId: unknown }> = Partial<Omit<T, 'expenseId'>> & { expenseId: T['expenseId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExpenses for edit and NewExpensesFormGroupInput for create.
 */
type ExpensesFormGroupInput = IExpenses | PartialWithRequiredKeyOf<NewExpenses>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IExpenses | NewExpenses> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

type ExpensesFormRawValue = FormValueOf<IExpenses>;

type NewExpensesFormRawValue = FormValueOf<NewExpenses>;

type ExpensesFormDefaults = Pick<NewExpenses, 'expenseId' | 'createdOn' | 'modifiedOn'>;

type ExpensesFormGroupContent = {
  expenseId: FormControl<ExpensesFormRawValue['expenseId'] | NewExpenses['expenseId']>;
  owner: FormControl<ExpensesFormRawValue['owner']>;
  content: FormControl<ExpensesFormRawValue['content']>;
  cost: FormControl<ExpensesFormRawValue['cost']>;
  tag: FormControl<ExpensesFormRawValue['tag']>;
  day: FormControl<ExpensesFormRawValue['day']>;
  createdBy: FormControl<ExpensesFormRawValue['createdBy']>;
  createdOn: FormControl<ExpensesFormRawValue['createdOn']>;
  modifiedBy: FormControl<ExpensesFormRawValue['modifiedBy']>;
  modifiedOn: FormControl<ExpensesFormRawValue['modifiedOn']>;
  types: FormControl<ExpensesFormRawValue['types']>;
  profiles: FormControl<ExpensesFormRawValue['profiles']>;
};

export type ExpensesFormGroup = FormGroup<ExpensesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExpensesFormService {
  createExpensesFormGroup(expenses: ExpensesFormGroupInput = { expenseId: null }): ExpensesFormGroup {
    const expensesRawValue = this.convertExpensesToExpensesRawValue({
      ...this.getFormDefaults(),
      ...expenses,
    });
    return new FormGroup<ExpensesFormGroupContent>({
      expenseId: new FormControl(
        { value: expensesRawValue.expenseId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      owner: new FormControl(expensesRawValue.owner),
      content: new FormControl(expensesRawValue.content),
      cost: new FormControl(expensesRawValue.cost),
      tag: new FormControl(expensesRawValue.tag),
      day: new FormControl(expensesRawValue.day),
      createdBy: new FormControl(expensesRawValue.createdBy),
      createdOn: new FormControl(expensesRawValue.createdOn),
      modifiedBy: new FormControl(expensesRawValue.modifiedBy),
      modifiedOn: new FormControl(expensesRawValue.modifiedOn),
      types: new FormControl(expensesRawValue.types),
      profiles: new FormControl(expensesRawValue.profiles),
    });
  }

  getExpenses(form: ExpensesFormGroup): IExpenses | NewExpenses {
    return this.convertExpensesRawValueToExpenses(form.getRawValue() as ExpensesFormRawValue | NewExpensesFormRawValue);
  }

  resetForm(form: ExpensesFormGroup, expenses: ExpensesFormGroupInput): void {
    const expensesRawValue = this.convertExpensesToExpensesRawValue({ ...this.getFormDefaults(), ...expenses });
    form.reset(
      {
        ...expensesRawValue,
        expenseId: { value: expensesRawValue.expenseId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExpensesFormDefaults {
    const currentTime = dayjs();

    return {
      expenseId: null,
      createdOn: currentTime,
      modifiedOn: currentTime,
    };
  }

  private convertExpensesRawValueToExpenses(rawExpenses: ExpensesFormRawValue | NewExpensesFormRawValue): IExpenses | NewExpenses {
    return {
      ...rawExpenses,
      createdOn: dayjs(rawExpenses.createdOn, DATE_TIME_FORMAT),
      modifiedOn: dayjs(rawExpenses.modifiedOn, DATE_TIME_FORMAT),
    };
  }

  private convertExpensesToExpensesRawValue(
    expenses: IExpenses | (Partial<NewExpenses> & ExpensesFormDefaults)
  ): ExpensesFormRawValue | PartialWithRequiredKeyOf<NewExpensesFormRawValue> {
    return {
      ...expenses,
      createdOn: expenses.createdOn ? expenses.createdOn.format(DATE_TIME_FORMAT) : undefined,
      modifiedOn: expenses.modifiedOn ? expenses.modifiedOn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
