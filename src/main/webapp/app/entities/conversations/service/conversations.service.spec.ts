import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IConversations } from '../conversations.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../conversations.test-samples';

import { ConversationsService, RestConversations } from './conversations.service';

const requireRestSample: RestConversations = {
  ...sampleWithRequiredData,
  timestamp: sampleWithRequiredData.timestamp?.toJSON(),
};

describe('Conversations Service', () => {
  let service: ConversationsService;
  let httpMock: HttpTestingController;
  let expectedResult: IConversations | IConversations[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ConversationsService);
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

    it('should create a Conversations', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const conversations = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(conversations).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Conversations', () => {
      const conversations = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(conversations).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Conversations', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Conversations', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Conversations', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addConversationsToCollectionIfMissing', () => {
      it('should add a Conversations to an empty array', () => {
        const conversations: IConversations = sampleWithRequiredData;
        expectedResult = service.addConversationsToCollectionIfMissing([], conversations);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversations);
      });

      it('should not add a Conversations to an array that contains it', () => {
        const conversations: IConversations = sampleWithRequiredData;
        const conversationsCollection: IConversations[] = [
          {
            ...conversations,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addConversationsToCollectionIfMissing(conversationsCollection, conversations);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Conversations to an array that doesn't contain it", () => {
        const conversations: IConversations = sampleWithRequiredData;
        const conversationsCollection: IConversations[] = [sampleWithPartialData];
        expectedResult = service.addConversationsToCollectionIfMissing(conversationsCollection, conversations);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversations);
      });

      it('should add only unique Conversations to an array', () => {
        const conversationsArray: IConversations[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const conversationsCollection: IConversations[] = [sampleWithRequiredData];
        expectedResult = service.addConversationsToCollectionIfMissing(conversationsCollection, ...conversationsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const conversations: IConversations = sampleWithRequiredData;
        const conversations2: IConversations = sampleWithPartialData;
        expectedResult = service.addConversationsToCollectionIfMissing([], conversations, conversations2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(conversations);
        expect(expectedResult).toContain(conversations2);
      });

      it('should accept null and undefined values', () => {
        const conversations: IConversations = sampleWithRequiredData;
        expectedResult = service.addConversationsToCollectionIfMissing([], null, conversations, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(conversations);
      });

      it('should return initial array if no Conversations is added', () => {
        const conversationsCollection: IConversations[] = [sampleWithRequiredData];
        expectedResult = service.addConversationsToCollectionIfMissing(conversationsCollection, undefined, null);
        expect(expectedResult).toEqual(conversationsCollection);
      });
    });

    describe('compareConversations', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareConversations(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { conversationId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareConversations(entity1, entity2);
        const compareResult2 = service.compareConversations(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { conversationId: 123 };
        const entity2 = { conversationId: 456 };

        const compareResult1 = service.compareConversations(entity1, entity2);
        const compareResult2 = service.compareConversations(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { conversationId: 123 };
        const entity2 = { conversationId: 123 };

        const compareResult1 = service.compareConversations(entity1, entity2);
        const compareResult2 = service.compareConversations(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
