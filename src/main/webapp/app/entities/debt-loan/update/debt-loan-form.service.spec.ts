import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../debt-loan.test-samples';

import { DebtLoanFormService } from './debt-loan-form.service';

describe('DebtLoan Form Service', () => {
  let service: DebtLoanFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DebtLoanFormService);
  });

  describe('Service methods', () => {
    describe('createDebtLoanFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDebtLoanFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            loanUserId: expect.any(Object),
            debtUserId: expect.any(Object),
            cost: expect.any(Object),
            deadline: expect.any(Object),
            datOfPayment: expect.any(Object),
            debts: expect.any(Object),
            loans: expect.any(Object),
          })
        );
      });

      it('passing IDebtLoan should create a new form with FormGroup', () => {
        const formGroup = service.createDebtLoanFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            loanUserId: expect.any(Object),
            debtUserId: expect.any(Object),
            cost: expect.any(Object),
            deadline: expect.any(Object),
            datOfPayment: expect.any(Object),
            debts: expect.any(Object),
            loans: expect.any(Object),
          })
        );
      });
    });

    describe('getDebtLoan', () => {
      it('should return NewDebtLoan for default DebtLoan initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDebtLoanFormGroup(sampleWithNewData);

        const debtLoan = service.getDebtLoan(formGroup) as any;

        expect(debtLoan).toMatchObject(sampleWithNewData);
      });

      it('should return NewDebtLoan for empty DebtLoan initial value', () => {
        const formGroup = service.createDebtLoanFormGroup();

        const debtLoan = service.getDebtLoan(formGroup) as any;

        expect(debtLoan).toMatchObject({});
      });

      it('should return IDebtLoan', () => {
        const formGroup = service.createDebtLoanFormGroup(sampleWithRequiredData);

        const debtLoan = service.getDebtLoan(formGroup) as any;

        expect(debtLoan).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDebtLoan should not enable id FormControl', () => {
        const formGroup = service.createDebtLoanFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDebtLoan should disable id FormControl', () => {
        const formGroup = service.createDebtLoanFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
