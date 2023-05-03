import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TypesFormService, TypesFormGroup } from './types-form.service';
import { ITypes } from '../types.model';
import { TypesService } from '../service/types.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-types-update',
  templateUrl: './types-update.component.html',
})
export class TypesUpdateComponent implements OnInit {
  isSaving = false;
  types: ITypes | null = null;

  profilesSharedCollection: IProfiles[] = [];

  editForm: TypesFormGroup = this.typesFormService.createTypesFormGroup();

  constructor(
    protected typesService: TypesService,
    protected typesFormService: TypesFormService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ types }) => {
      this.types = types;
      if (types) {
        this.updateForm(types);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const types = this.typesFormService.getTypes(this.editForm);
    if (types.typeId !== null) {
      this.subscribeToSaveResponse(this.typesService.update(types));
    } else {
      this.subscribeToSaveResponse(this.typesService.create(types));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypes>>): void {
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

  protected updateForm(types: ITypes): void {
    this.types = types;
    this.typesFormService.resetForm(this.editForm, types);

    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      types.profiles
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfiles[]) => this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.types?.profiles))
      )
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
