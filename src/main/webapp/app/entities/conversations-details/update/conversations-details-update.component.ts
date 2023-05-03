import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ConversationsDetailsFormService, ConversationsDetailsFormGroup } from './conversations-details-form.service';
import { IConversationsDetails } from '../conversations-details.model';
import { ConversationsDetailsService } from '../service/conversations-details.service';
import { IConversations } from 'app/entities/conversations/conversations.model';
import { ConversationsService } from 'app/entities/conversations/service/conversations.service';

@Component({
  selector: 'jhi-conversations-details-update',
  templateUrl: './conversations-details-update.component.html',
})
export class ConversationsDetailsUpdateComponent implements OnInit {
  isSaving = false;
  conversationsDetails: IConversationsDetails | null = null;

  conversationsSharedCollection: IConversations[] = [];

  editForm: ConversationsDetailsFormGroup = this.conversationsDetailsFormService.createConversationsDetailsFormGroup();

  constructor(
    protected conversationsDetailsService: ConversationsDetailsService,
    protected conversationsDetailsFormService: ConversationsDetailsFormService,
    protected conversationsService: ConversationsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareConversations = (o1: IConversations | null, o2: IConversations | null): boolean =>
    this.conversationsService.compareConversations(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conversationsDetails }) => {
      this.conversationsDetails = conversationsDetails;
      if (conversationsDetails) {
        this.updateForm(conversationsDetails);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conversationsDetails = this.conversationsDetailsFormService.getConversationsDetails(this.editForm);
    if (conversationsDetails.id !== null) {
      this.subscribeToSaveResponse(this.conversationsDetailsService.update(conversationsDetails));
    } else {
      this.subscribeToSaveResponse(this.conversationsDetailsService.create(conversationsDetails));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConversationsDetails>>): void {
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

  protected updateForm(conversationsDetails: IConversationsDetails): void {
    this.conversationsDetails = conversationsDetails;
    this.conversationsDetailsFormService.resetForm(this.editForm, conversationsDetails);

    this.conversationsSharedCollection = this.conversationsService.addConversationsToCollectionIfMissing<IConversations>(
      this.conversationsSharedCollection,
      conversationsDetails.conversations
    );
  }

  protected loadRelationshipsOptions(): void {
    this.conversationsService
      .query()
      .pipe(map((res: HttpResponse<IConversations[]>) => res.body ?? []))
      .pipe(
        map((conversations: IConversations[]) =>
          this.conversationsService.addConversationsToCollectionIfMissing<IConversations>(
            conversations,
            this.conversationsDetails?.conversations
          )
        )
      )
      .subscribe((conversations: IConversations[]) => (this.conversationsSharedCollection = conversations));
  }
}
