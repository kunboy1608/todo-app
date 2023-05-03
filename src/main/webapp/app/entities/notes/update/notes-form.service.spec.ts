import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../notes.test-samples';

import { NotesFormService } from './notes-form.service';

describe('Notes Form Service', () => {
  let service: NotesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotesFormService);
  });

  describe('Service methods', () => {
    describe('createNotesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createNotesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            noteId: expect.any(Object),
            owner: expect.any(Object),
            content: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });

      it('passing INotes should create a new form with FormGroup', () => {
        const formGroup = service.createNotesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            noteId: expect.any(Object),
            owner: expect.any(Object),
            content: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });
    });

    describe('getNotes', () => {
      it('should return NewNotes for default Notes initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createNotesFormGroup(sampleWithNewData);

        const notes = service.getNotes(formGroup) as any;

        expect(notes).toMatchObject(sampleWithNewData);
      });

      it('should return NewNotes for empty Notes initial value', () => {
        const formGroup = service.createNotesFormGroup();

        const notes = service.getNotes(formGroup) as any;

        expect(notes).toMatchObject({});
      });

      it('should return INotes', () => {
        const formGroup = service.createNotesFormGroup(sampleWithRequiredData);

        const notes = service.getNotes(formGroup) as any;

        expect(notes).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing INotes should not enable noteId FormControl', () => {
        const formGroup = service.createNotesFormGroup();
        expect(formGroup.controls.noteId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.noteId.disabled).toBe(true);
      });

      it('passing NewNotes should disable noteId FormControl', () => {
        const formGroup = service.createNotesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.noteId.disabled).toBe(true);

        service.resetForm(formGroup, { noteId: null });

        expect(formGroup.controls.noteId.disabled).toBe(true);
      });
    });
  });
});
