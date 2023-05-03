import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../conversations-details.test-samples';

import { ConversationsDetailsFormService } from './conversations-details-form.service';

describe('ConversationsDetails Form Service', () => {
  let service: ConversationsDetailsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConversationsDetailsFormService);
  });

  describe('Service methods', () => {
    describe('createConversationsDetailsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConversationsDetailsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            isGroup: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
            conversations: expect.any(Object),
          })
        );
      });

      it('passing IConversationsDetails should create a new form with FormGroup', () => {
        const formGroup = service.createConversationsDetailsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            isGroup: expect.any(Object),
            createdBy: expect.any(Object),
            createdOn: expect.any(Object),
            modifiedBy: expect.any(Object),
            modifiedOn: expect.any(Object),
            conversations: expect.any(Object),
          })
        );
      });
    });

    describe('getConversationsDetails', () => {
      it('should return NewConversationsDetails for default ConversationsDetails initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createConversationsDetailsFormGroup(sampleWithNewData);

        const conversationsDetails = service.getConversationsDetails(formGroup) as any;

        expect(conversationsDetails).toMatchObject(sampleWithNewData);
      });

      it('should return NewConversationsDetails for empty ConversationsDetails initial value', () => {
        const formGroup = service.createConversationsDetailsFormGroup();

        const conversationsDetails = service.getConversationsDetails(formGroup) as any;

        expect(conversationsDetails).toMatchObject({});
      });

      it('should return IConversationsDetails', () => {
        const formGroup = service.createConversationsDetailsFormGroup(sampleWithRequiredData);

        const conversationsDetails = service.getConversationsDetails(formGroup) as any;

        expect(conversationsDetails).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConversationsDetails should not enable id FormControl', () => {
        const formGroup = service.createConversationsDetailsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewConversationsDetails should disable id FormControl', () => {
        const formGroup = service.createConversationsDetailsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
