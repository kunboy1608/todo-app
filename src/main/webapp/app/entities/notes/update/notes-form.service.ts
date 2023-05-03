import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { INotes, NewNotes } from '../notes.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { noteId: unknown }> = Partial<Omit<T, 'noteId'>> & { noteId: T['noteId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotes for edit and NewNotesFormGroupInput for create.
 */
type NotesFormGroupInput = INotes | PartialWithRequiredKeyOf<NewNotes>;

type NotesFormDefaults = Pick<NewNotes, 'noteId'>;

type NotesFormGroupContent = {
  noteId: FormControl<INotes['noteId'] | NewNotes['noteId']>;
  owner: FormControl<INotes['owner']>;
  content: FormControl<INotes['content']>;
  profiles: FormControl<INotes['profiles']>;
};

export type NotesFormGroup = FormGroup<NotesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotesFormService {
  createNotesFormGroup(notes: NotesFormGroupInput = { noteId: null }): NotesFormGroup {
    const notesRawValue = {
      ...this.getFormDefaults(),
      ...notes,
    };
    return new FormGroup<NotesFormGroupContent>({
      noteId: new FormControl(
        { value: notesRawValue.noteId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      owner: new FormControl(notesRawValue.owner),
      content: new FormControl(notesRawValue.content),
      profiles: new FormControl(notesRawValue.profiles),
    });
  }

  getNotes(form: NotesFormGroup): INotes | NewNotes {
    return form.getRawValue() as INotes | NewNotes;
  }

  resetForm(form: NotesFormGroup, notes: NotesFormGroupInput): void {
    const notesRawValue = { ...this.getFormDefaults(), ...notes };
    form.reset(
      {
        ...notesRawValue,
        noteId: { value: notesRawValue.noteId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): NotesFormDefaults {
    return {
      noteId: null,
    };
  }
}
