import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDebtLoan } from '../debt-loan.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../debt-loan.test-samples';

import { DebtLoanService, RestDebtLoan } from './debt-loan.service';

const requireRestSample: RestDebtLoan = {
  ...sampleWithRequiredData,
  deadline: sampleWithRequiredData.deadline?.toJSON(),
  datOfPayment: sampleWithRequiredData.datOfPayment?.toJSON(),
};

describe('DebtLoan Service', () => {
  let service: DebtLoanService;
  let httpMock: HttpTestingController;
  let expectedResult: IDebtLoan | IDebtLoan[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DebtLoanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a DebtLoan', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const debtLoan = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(debtLoan).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DebtLoan', () => {
      const debtLoan = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(debtLoan).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DebtLoan', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DebtLoan', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DebtLoan', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDebtLoanToCollectionIfMissing', () => {
      it('should add a DebtLoan to an empty array', () => {
        const debtLoan: IDebtLoan = sampleWithRequiredData;
        expectedResult = service.addDebtLoanToCollectionIfMissing([], debtLoan);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(debtLoan);
      });

      it('should not add a DebtLoan to an array that contains it', () => {
        const debtLoan: IDebtLoan = sampleWithRequiredData;
        const debtLoanCollection: IDebtLoan[] = [
          {
            ...debtLoan,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDebtLoanToCollectionIfMissing(debtLoanCollection, debtLoan);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DebtLoan to an array that doesn't contain it", () => {
        const debtLoan: IDebtLoan = sampleWithRequiredData;
        const debtLoanCollection: IDebtLoan[] = [sampleWithPartialData];
        expectedResult = service.addDebtLoanToCollectionIfMissing(debtLoanCollection, debtLoan);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(debtLoan);
      });

      it('should add only unique DebtLoan to an array', () => {
        const debtLoanArray: IDebtLoan[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const debtLoanCollection: IDebtLoan[] = [sampleWithRequiredData];
        expectedResult = service.addDebtLoanToCollectionIfMissing(debtLoanCollection, ...debtLoanArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const debtLoan: IDebtLoan = sampleWithRequiredData;
        const debtLoan2: IDebtLoan = sampleWithPartialData;
        expectedResult = service.addDebtLoanToCollectionIfMissing([], debtLoan, debtLoan2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(debtLoan);
        expect(expectedResult).toContain(debtLoan2);
      });

      it('should accept null and undefined values', () => {
        const debtLoan: IDebtLoan = sampleWithRequiredData;
        expectedResult = service.addDebtLoanToCollectionIfMissing([], null, debtLoan, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(debtLoan);
      });

      it('should return initial array if no DebtLoan is added', () => {
        const debtLoanCollection: IDebtLoan[] = [sampleWithRequiredData];
        expectedResult = service.addDebtLoanToCollectionIfMissing(debtLoanCollection, undefined, null);
        expect(expectedResult).toEqual(debtLoanCollection);
      });
    });

    describe('compareDebtLoan', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDebtLoan(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDebtLoan(entity1, entity2);
        const compareResult2 = service.compareDebtLoan(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDebtLoan(entity1, entity2);
        const compareResult2 = service.compareDebtLoan(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDebtLoan(entity1, entity2);
        const compareResult2 = service.compareDebtLoan(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
