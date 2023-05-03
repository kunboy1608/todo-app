import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INotes } from '../notes.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../notes.test-samples';

import { NotesService } from './notes.service';

const requireRestSample: INotes = {
  ...sampleWithRequiredData,
};

describe('Notes Service', () => {
  let service: NotesService;
  let httpMock: HttpTestingController;
  let expectedResult: INotes | INotes[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NotesService);
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

    it('should create a Notes', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const notes = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(notes).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Notes', () => {
      const notes = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(notes).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Notes', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Notes', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Notes', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addNotesToCollectionIfMissing', () => {
      it('should add a Notes to an empty array', () => {
        const notes: INotes = sampleWithRequiredData;
        expectedResult = service.addNotesToCollectionIfMissing([], notes);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notes);
      });

      it('should not add a Notes to an array that contains it', () => {
        const notes: INotes = sampleWithRequiredData;
        const notesCollection: INotes[] = [
          {
            ...notes,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, notes);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Notes to an array that doesn't contain it", () => {
        const notes: INotes = sampleWithRequiredData;
        const notesCollection: INotes[] = [sampleWithPartialData];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, notes);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notes);
      });

      it('should add only unique Notes to an array', () => {
        const notesArray: INotes[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const notesCollection: INotes[] = [sampleWithRequiredData];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, ...notesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const notes: INotes = sampleWithRequiredData;
        const notes2: INotes = sampleWithPartialData;
        expectedResult = service.addNotesToCollectionIfMissing([], notes, notes2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(notes);
        expect(expectedResult).toContain(notes2);
      });

      it('should accept null and undefined values', () => {
        const notes: INotes = sampleWithRequiredData;
        expectedResult = service.addNotesToCollectionIfMissing([], null, notes, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(notes);
      });

      it('should return initial array if no Notes is added', () => {
        const notesCollection: INotes[] = [sampleWithRequiredData];
        expectedResult = service.addNotesToCollectionIfMissing(notesCollection, undefined, null);
        expect(expectedResult).toEqual(notesCollection);
      });
    });

    describe('compareNotes', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareNotes(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { noteId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareNotes(entity1, entity2);
        const compareResult2 = service.compareNotes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { noteId: 123 };
        const entity2 = { noteId: 456 };

        const compareResult1 = service.compareNotes(entity1, entity2);
        const compareResult2 = service.compareNotes(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { noteId: 123 };
        const entity2 = { noteId: 123 };

        const compareResult1 = service.compareNotes(entity1, entity2);
        const compareResult2 = service.compareNotes(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
