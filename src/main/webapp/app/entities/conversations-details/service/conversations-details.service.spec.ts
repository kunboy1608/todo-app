import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IConversationsDetails } from '../conversations-details.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../conversations-details.test-samples';

import { ConversationsDetailsService, RestConversationsDetails } from './conversations-details.service';

const requireRestSample: RestConversationsDetails = {
  ...sampleWithRequiredData,
  createdOn: sampleWithRequiredData.createdOn?.toJSON(),
  modifiedOn: sampleWithRequiredData.modifiedOn?.toJSON(),
};

describe('ConversationsDetails Service', () => {
  let service: ConversationsDetailsService;
  let httpMock: HttpTestingController;
  let expectedResult: IConversationsDetails | IConversationsDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConversationsDetailsService);
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

    it('should create a ConversationsDetails', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const conversationsDetails = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(conversationsDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ConversationsDetails', () => {
      const conversationsDetails = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(conversationsDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ConversationsDetails', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ConversationsDetails', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ConversationsDetails', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addConversationsDetailsToCollectionIfMissing', () => {
      it('should add a ConversationsDetails to an empty array', () => {
        const conversationsDetails: IConversationsDetails = sampleWithRequiredData;
        expectedResult = service.addConversationsDetailsToCollectionIfMissing([], conversationsDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversationsDetails);
      });

      it('should not add a ConversationsDetails to an array that contains it', () => {
        const conversationsDetails: IConversationsDetails = sampleWithRequiredData;
        const conversationsDetailsCollection: IConversationsDetails[] = [
          {
            ...conversationsDetails,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConversationsDetailsToCollectionIfMissing(conversationsDetailsCollection, conversationsDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ConversationsDetails to an array that doesn't contain it", () => {
        const conversationsDetails: IConversationsDetails = sampleWithRequiredData;
        const conversationsDetailsCollection: IConversationsDetails[] = [sampleWithPartialData];
        expectedResult = service.addConversationsDetailsToCollectionIfMissing(conversationsDetailsCollection, conversationsDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversationsDetails);
      });

      it('should add only unique ConversationsDetails to an array', () => {
        const conversationsDetailsArray: IConversationsDetails[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conversationsDetailsCollection: IConversationsDetails[] = [sampleWithRequiredData];
        expectedResult = service.addConversationsDetailsToCollectionIfMissing(conversationsDetailsCollection, ...conversationsDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conversationsDetails: IConversationsDetails = sampleWithRequiredData;
        const conversationsDetails2: IConversationsDetails = sampleWithPartialData;
        expectedResult = service.addConversationsDetailsToCollectionIfMissing([], conversationsDetails, conversationsDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversationsDetails);
        expect(expectedResult).toContain(conversationsDetails2);
      });

      it('should accept null and undefined values', () => {
        const conversationsDetails: IConversationsDetails = sampleWithRequiredData;
        expectedResult = service.addConversationsDetailsToCollectionIfMissing([], null, conversationsDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversationsDetails);
      });

      it('should return initial array if no ConversationsDetails is added', () => {
        const conversationsDetailsCollection: IConversationsDetails[] = [sampleWithRequiredData];
        expectedResult = service.addConversationsDetailsToCollectionIfMissing(conversationsDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(conversationsDetailsCollection);
      });
    });

    describe('compareConversationsDetails', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConversationsDetails(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareConversationsDetails(entity1, entity2);
        const compareResult2 = service.compareConversationsDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareConversationsDetails(entity1, entity2);
        const compareResult2 = service.compareConversationsDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareConversationsDetails(entity1, entity2);
        const compareResult2 = service.compareConversationsDetails(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
