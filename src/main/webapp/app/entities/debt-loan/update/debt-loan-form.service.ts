import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDebtLoan, NewDebtLoan } from '../debt-loan.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDebtLoan for edit and NewDebtLoanFormGroupInput for create.
 */
type DebtLoanFormGroupInput = IDebtLoan | PartialWithRequiredKeyOf<NewDebtLoan>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDebtLoan | NewDebtLoan> = Omit<T, 'deadline' | 'datOfPayment'> & {
  deadline?: string | null;
  datOfPayment?: string | null;
};

type DebtLoanFormRawValue = FormValueOf<IDebtLoan>;

type NewDebtLoanFormRawValue = FormValueOf<NewDebtLoan>;

type DebtLoanFormDefaults = Pick<NewDebtLoan, 'id' | 'deadline' | 'datOfPayment'>;

type DebtLoanFormGroupContent = {
  id: FormControl<DebtLoanFormRawValue['id'] | NewDebtLoan['id']>;
  loanUserId: FormControl<DebtLoanFormRawValue['loanUserId']>;
  debtUserId: FormControl<DebtLoanFormRawValue['debtUserId']>;
  cost: FormControl<DebtLoanFormRawValue['cost']>;
  deadline: FormControl<DebtLoanFormRawValue['deadline']>;
  datOfPayment: FormControl<DebtLoanFormRawValue['datOfPayment']>;
  debts: FormControl<DebtLoanFormRawValue['debts']>;
  loans: FormControl<DebtLoanFormRawValue['loans']>;
};

export type DebtLoanFormGroup = FormGroup<DebtLoanFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DebtLoanFormService {
  createDebtLoanFormGroup(debtLoan: DebtLoanFormGroupInput = { id: null }): DebtLoanFormGroup {
    const debtLoanRawValue = this.convertDebtLoanToDebtLoanRawValue({
      ...this.getFormDefaults(),
      ...debtLoan,
    });
    return new FormGroup<DebtLoanFormGroupContent>({
      id: new FormControl(
        { value: debtLoanRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      loanUserId: new FormControl(debtLoanRawValue.loanUserId),
      debtUserId: new FormControl(debtLoanRawValue.debtUserId),
      cost: new FormControl(debtLoanRawValue.cost),
      deadline: new FormControl(debtLoanRawValue.deadline),
      datOfPayment: new FormControl(debtLoanRawValue.datOfPayment),
      debts: new FormControl(debtLoanRawValue.debts),
      loans: new FormControl(debtLoanRawValue.loans),
    });
  }

  getDebtLoan(form: DebtLoanFormGroup): IDebtLoan | NewDebtLoan {
    return this.convertDebtLoanRawValueToDebtLoan(form.getRawValue() as DebtLoanFormRawValue | NewDebtLoanFormRawValue);
  }

  resetForm(form: DebtLoanFormGroup, debtLoan: DebtLoanFormGroupInput): void {
    const debtLoanRawValue = this.convertDebtLoanToDebtLoanRawValue({ ...this.getFormDefaults(), ...debtLoan });
    form.reset(
      {
        ...debtLoanRawValue,
        id: { value: debtLoanRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DebtLoanFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      deadline: currentTime,
      datOfPayment: currentTime,
    };
  }

  private convertDebtLoanRawValueToDebtLoan(rawDebtLoan: DebtLoanFormRawValue | NewDebtLoanFormRawValue): IDebtLoan | NewDebtLoan {
    return {
      ...rawDebtLoan,
      deadline: dayjs(rawDebtLoan.deadline, DATE_TIME_FORMAT),
      datOfPayment: dayjs(rawDebtLoan.datOfPayment, DATE_TIME_FORMAT),
    };
  }

  private convertDebtLoanToDebtLoanRawValue(
    debtLoan: IDebtLoan | (Partial<NewDebtLoan> & DebtLoanFormDefaults)
  ): DebtLoanFormRawValue | PartialWithRequiredKeyOf<NewDebtLoanFormRawValue> {
    return {
      ...debtLoan,
      deadline: debtLoan.deadline ? debtLoan.deadline.format(DATE_TIME_FORMAT) : undefined,
      datOfPayment: debtLoan.datOfPayment ? debtLoan.datOfPayment.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
