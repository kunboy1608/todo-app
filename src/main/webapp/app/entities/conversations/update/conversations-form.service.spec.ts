import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../conversations.test-samples';

import { ConversationsFormService } from './conversations-form.service';

describe('Conversations Form Service', () => {
  let service: ConversationsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConversationsFormService);
  });

  describe('Service methods', () => {
    describe('createConversationsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createConversationsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            conversationId: expect.any(Object),
            timestamp: expect.any(Object),
            sender: expect.any(Object),
            receiver: expect.any(Object),
            message: expect.any(Object),
          })
        );
      });

      it('passing IConversations should create a new form with FormGroup', () => {
        const formGroup = service.createConversationsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            conversationId: expect.any(Object),
            timestamp: expect.any(Object),
            sender: expect.any(Object),
            receiver: expect.any(Object),
            message: expect.any(Object),
          })
        );
      });
    });

    describe('getConversations', () => {
      it('should return NewConversations for default Conversations initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createConversationsFormGroup(sampleWithNewData);

        const conversations = service.getConversations(formGroup) as any;

        expect(conversations).toMatchObject(sampleWithNewData);
      });

      it('should return NewConversations for empty Conversations initial value', () => {
        const formGroup = service.createConversationsFormGroup();

        const conversations = service.getConversations(formGroup) as any;

        expect(conversations).toMatchObject({});
      });

      it('should return IConversations', () => {
        const formGroup = service.createConversationsFormGroup(sampleWithRequiredData);

        const conversations = service.getConversations(formGroup) as any;

        expect(conversations).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IConversations should not enable conversationId FormControl', () => {
        const formGroup = service.createConversationsFormGroup();
        expect(formGroup.controls.conversationId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.conversationId.disabled).toBe(true);
      });

      it('passing NewConversations should disable conversationId FormControl', () => {
        const formGroup = service.createConversationsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.conversationId.disabled).toBe(true);

        service.resetForm(formGroup, { conversationId: null });

        expect(formGroup.controls.conversationId.disabled).toBe(true);
      });
    });
  });
});
