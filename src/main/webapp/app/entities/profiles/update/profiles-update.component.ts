import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ProfilesFormService, ProfilesFormGroup } from './profiles-form.service';
import { IProfiles } from '../profiles.model';
import { ProfilesService } from '../service/profiles.service';

@Component({
  selector: 'jhi-profiles-update',
  templateUrl: './profiles-update.component.html',
})
export class ProfilesUpdateComponent implements OnInit {
  isSaving = false;
  profiles: IProfiles | null = null;

  editForm: ProfilesFormGroup = this.profilesFormService.createProfilesFormGroup();

  constructor(
    protected profilesService: ProfilesService,
    protected profilesFormService: ProfilesFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profiles }) => {
      this.profiles = profiles;
      if (profiles) {
        this.updateForm(profiles);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const profiles = this.profilesFormService.getProfiles(this.editForm);
    if (profiles.profileId !== null) {
      this.subscribeToSaveResponse(this.profilesService.update(profiles));
    } else {
      this.subscribeToSaveResponse(this.profilesService.create(profiles));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProfiles>>): void {
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

  protected updateForm(profiles: IProfiles): void {
    this.profiles = profiles;
    this.profilesFormService.resetForm(this.editForm, profiles);
  }
}
