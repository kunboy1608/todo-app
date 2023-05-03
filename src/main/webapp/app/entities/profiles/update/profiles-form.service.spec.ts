import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../profiles.test-samples';

import { ProfilesFormService } from './profiles-form.service';

describe('Profiles Form Service', () => {
  let service: ProfilesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfilesFormService);
  });

  describe('Service methods', () => {
    describe('createProfilesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfilesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            profileId: expect.any(Object),
            username: expect.any(Object),
            nickname: expect.any(Object),
            birthday: expect.any(Object),
            bio: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
          })
        );
      });

      it('passing IProfiles should create a new form with FormGroup', () => {
        const formGroup = service.createProfilesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            profileId: expect.any(Object),
            username: expect.any(Object),
            nickname: expect.any(Object),
            birthday: expect.any(Object),
            bio: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
          })
        );
      });
    });

    describe('getProfiles', () => {
      it('should return NewProfiles for default Profiles initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createProfilesFormGroup(sampleWithNewData);

        const profiles = service.getProfiles(formGroup) as any;

        expect(profiles).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfiles for empty Profiles initial value', () => {
        const formGroup = service.createProfilesFormGroup();

        const profiles = service.getProfiles(formGroup) as any;

        expect(profiles).toMatchObject({});
      });

      it('should return IProfiles', () => {
        const formGroup = service.createProfilesFormGroup(sampleWithRequiredData);

        const profiles = service.getProfiles(formGroup) as any;

        expect(profiles).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfiles should not enable profileId FormControl', () => {
        const formGroup = service.createProfilesFormGroup();
        expect(formGroup.controls.profileId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.profileId.disabled).toBe(true);
      });

      it('passing NewProfiles should disable profileId FormControl', () => {
        const formGroup = service.createProfilesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.profileId.disabled).toBe(true);

        service.resetForm(formGroup, { profileId: null });

        expect(formGroup.controls.profileId.disabled).toBe(true);
      });
    });
  });
});
