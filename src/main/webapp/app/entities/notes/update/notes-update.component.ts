import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { NotesFormService, NotesFormGroup } from './notes-form.service';
import { INotes } from '../notes.model';
import { NotesService } from '../service/notes.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-notes-update',
  templateUrl: './notes-update.component.html',
})
export class NotesUpdateComponent implements OnInit {
  isSaving = false;
  notes: INotes | null = null;

  profilesSharedCollection: IProfiles[] = [];

  editForm: NotesFormGroup = this.notesFormService.createNotesFormGroup();

  constructor(
    protected notesService: NotesService,
    protected notesFormService: NotesFormService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notes }) => {
      this.notes = notes;
      if (notes) {
        this.updateForm(notes);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notes = this.notesFormService.getNotes(this.editForm);
    if (notes.noteId !== null) {
      this.subscribeToSaveResponse(this.notesService.update(notes));
    } else {
      this.subscribeToSaveResponse(this.notesService.create(notes));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotes>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(notes: INotes): void {
    this.notes = notes;
    this.notesFormService.resetForm(this.editForm, notes);

    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      notes.profiles
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfiles[]) => this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.notes?.profiles))
      )
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
