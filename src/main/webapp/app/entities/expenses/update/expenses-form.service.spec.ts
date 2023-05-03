import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../expenses.test-samples';

import { ExpensesFormService } from './expenses-form.service';

describe('Expenses Form Service', () => {
  let service: ExpensesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExpensesFormService);
  });

  describe('Service methods', () => {
    describe('createExpensesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExpensesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            expenseId: expect.any(Object),
            owner: expect.any(Object),
            content: expect.any(Object),
            cost: expect.any(Object),
            tag: expect.any(Object),
            day: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
            types: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });

      it('passing IExpenses should create a new form with FormGroup', () => {
        const formGroup = service.createExpensesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            expenseId: expect.any(Object),
            owner: expect.any(Object),
            content: expect.any(Object),
            cost: expect.any(Object),
            tag: expect.any(Object),
            day: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
            types: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });
    });

    describe('getExpenses', () => {
      it('should return NewExpenses for default Expenses initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExpensesFormGroup(sampleWithNewData);

        const expenses = service.getExpenses(formGroup) as any;

        expect(expenses).toMatchObject(sampleWithNewData);
      });

      it('should return NewExpenses for empty Expenses initial value', () => {
        const formGroup = service.createExpensesFormGroup();

        const expenses = service.getExpenses(formGroup) as any;

        expect(expenses).toMatchObject({});
      });

      it('should return IExpenses', () => {
        const formGroup = service.createExpensesFormGroup(sampleWithRequiredData);

        const expenses = service.getExpenses(formGroup) as any;

        expect(expenses).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExpenses should not enable expenseId FormControl', () => {
        const formGroup = service.createExpensesFormGroup();
        expect(formGroup.controls.expenseId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.expenseId.disabled).toBe(true);
      });

      it('passing NewExpenses should disable expenseId FormControl', () => {
        const formGroup = service.createExpensesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.expenseId.disabled).toBe(true);

        service.resetForm(formGroup, { expenseId: null });

        expect(formGroup.controls.expenseId.disabled).toBe(true);
      });
    });
  });
});
