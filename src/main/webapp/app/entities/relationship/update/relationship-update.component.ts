import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { RelationshipFormService, RelationshipFormGroup } from './relationship-form.service';
import { IRelationship } from '../relationship.model';
import { RelationshipService } from '../service/relationship.service';

@Component({
  selector: 'jhi-relationship-update',
  templateUrl: './relationship-update.component.html',
})
export class RelationshipUpdateComponent implements OnInit {
  isSaving = false;
  relationship: IRelationship | null = null;

  editForm: RelationshipFormGroup = this.relationshipFormService.createRelationshipFormGroup();

  constructor(
    protected relationshipService: RelationshipService,
    protected relationshipFormService: RelationshipFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationship }) => {
      this.relationship = relationship;
      if (relationship) {
        this.updateForm(relationship);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const relationship = this.relationshipFormService.getRelationship(this.editForm);
    if (relationship.relationshipId !== null) {
      this.subscribeToSaveResponse(this.relationshipService.update(relationship));
    } else {
      this.subscribeToSaveResponse(this.relationshipService.create(relationship));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRelationship>>): void {
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

  protected updateForm(relationship: IRelationship): void {
    this.relationship = relationship;
    this.relationshipFormService.resetForm(this.editForm, relationship);
  }
}
