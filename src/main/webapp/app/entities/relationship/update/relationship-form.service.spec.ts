import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../relationship.test-samples';

import { RelationshipFormService } from './relationship-form.service';

describe('Relationship Form Service', () => {
  let service: RelationshipFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RelationshipFormService);
  });

  describe('Service methods', () => {
    describe('createRelationshipFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRelationshipFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            relationshipId: expect.any(Object),
            owner: expect.any(Object),
            partner: expect.any(Object),
            status: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
          })
        );
      });

      it('passing IRelationship should create a new form with FormGroup', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            relationshipId: expect.any(Object),
            owner: expect.any(Object),
            partner: expect.any(Object),
            status: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
          })
        );
      });
    });

    describe('getRelationship', () => {
      it('should return NewRelationship for default Relationship initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRelationshipFormGroup(sampleWithNewData);

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject(sampleWithNewData);
      });

      it('should return NewRelationship for empty Relationship initial value', () => {
        const formGroup = service.createRelationshipFormGroup();

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject({});
      });

      it('should return IRelationship', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);

        const relationship = service.getRelationship(formGroup) as any;

        expect(relationship).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRelationship should not enable relationshipId FormControl', () => {
        const formGroup = service.createRelationshipFormGroup();
        expect(formGroup.controls.relationshipId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.relationshipId.disabled).toBe(true);
      });

      it('passing NewRelationship should disable relationshipId FormControl', () => {
        const formGroup = service.createRelationshipFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.relationshipId.disabled).toBe(true);

        service.resetForm(formGroup, { relationshipId: null });

        expect(formGroup.controls.relationshipId.disabled).toBe(true);
      });
    });
  });
});
