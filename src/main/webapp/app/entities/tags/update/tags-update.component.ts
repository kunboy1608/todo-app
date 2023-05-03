import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TagsFormService, TagsFormGroup } from './tags-form.service';
import { ITags } from '../tags.model';
import { TagsService } from '../service/tags.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-tags-update',
  templateUrl: './tags-update.component.html',
})
export class TagsUpdateComponent implements OnInit {
  isSaving = false;
  tags: ITags | null = null;

  profilesSharedCollection: IProfiles[] = [];

  editForm: TagsFormGroup = this.tagsFormService.createTagsFormGroup();

  constructor(
    protected tagsService: TagsService,
    protected tagsFormService: TagsFormService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tags }) => {
      this.tags = tags;
      if (tags) {
        this.updateForm(tags);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tags = this.tagsFormService.getTags(this.editForm);
    if (tags.tagId !== null) {
      this.subscribeToSaveResponse(this.tagsService.update(tags));
    } else {
      this.subscribeToSaveResponse(this.tagsService.create(tags));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITags>>): void {
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

  protected updateForm(tags: ITags): void {
    this.tags = tags;
    this.tagsFormService.resetForm(this.editForm, tags);

    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      tags.profiles
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(map((profiles: IProfiles[]) => this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.tags?.profiles)))
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
