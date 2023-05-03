import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IProfiles } from '../profiles.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../profiles.test-samples';

import { ProfilesService, RestProfiles } from './profiles.service';

const requireRestSample: RestProfiles = {
  ...sampleWithRequiredData,
  birthday: sampleWithRequiredData.birthday?.format(DATE_FORMAT),
  createdOn: sampleWithRequiredData.createdOn?.toJSON(),
  modifiedOn: sampleWithRequiredData.modifiedOn?.toJSON(),
};

describe('Profiles Service', () => {
  let service: ProfilesService;
  let httpMock: HttpTestingController;
  let expectedResult: IProfiles | IProfiles[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProfilesService);
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

    it('should create a Profiles', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const profiles = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(profiles).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Profiles', () => {
      const profiles = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(profiles).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Profiles', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Profiles', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Profiles', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProfilesToCollectionIfMissing', () => {
      it('should add a Profiles to an empty array', () => {
        const profiles: IProfiles = sampleWithRequiredData;
        expectedResult = service.addProfilesToCollectionIfMissing([], profiles);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profiles);
      });

      it('should not add a Profiles to an array that contains it', () => {
        const profiles: IProfiles = sampleWithRequiredData;
        const profilesCollection: IProfiles[] = [
          {
            ...profiles,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProfilesToCollectionIfMissing(profilesCollection, profiles);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Profiles to an array that doesn't contain it", () => {
        const profiles: IProfiles = sampleWithRequiredData;
        const profilesCollection: IProfiles[] = [sampleWithPartialData];
        expectedResult = service.addProfilesToCollectionIfMissing(profilesCollection, profiles);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profiles);
      });

      it('should add only unique Profiles to an array', () => {
        const profilesArray: IProfiles[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const profilesCollection: IProfiles[] = [sampleWithRequiredData];
        expectedResult = service.addProfilesToCollectionIfMissing(profilesCollection, ...profilesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const profiles: IProfiles = sampleWithRequiredData;
        const profiles2: IProfiles = sampleWithPartialData;
        expectedResult = service.addProfilesToCollectionIfMissing([], profiles, profiles2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(profiles);
        expect(expectedResult).toContain(profiles2);
      });

      it('should accept null and undefined values', () => {
        const profiles: IProfiles = sampleWithRequiredData;
        expectedResult = service.addProfilesToCollectionIfMissing([], null, profiles, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(profiles);
      });

      it('should return initial array if no Profiles is added', () => {
        const profilesCollection: IProfiles[] = [sampleWithRequiredData];
        expectedResult = service.addProfilesToCollectionIfMissing(profilesCollection, undefined, null);
        expect(expectedResult).toEqual(profilesCollection);
      });
    });

    describe('compareProfiles', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProfiles(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { profileId: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProfiles(entity1, entity2);
        const compareResult2 = service.compareProfiles(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { profileId: 123 };
        const entity2 = { profileId: 456 };

        const compareResult1 = service.compareProfiles(entity1, entity2);
        const compareResult2 = service.compareProfiles(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { profileId: 123 };
        const entity2 = { profileId: 123 };

        const compareResult1 = service.compareProfiles(entity1, entity2);
        const compareResult2 = service.compareProfiles(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
