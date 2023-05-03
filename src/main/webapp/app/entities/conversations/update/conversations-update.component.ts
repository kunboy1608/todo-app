import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ConversationsFormService, ConversationsFormGroup } from './conversations-form.service';
import { IConversations } from '../conversations.model';
import { ConversationsService } from '../service/conversations.service';

@Component({
  selector: 'jhi-conversations-update',
  templateUrl: './conversations-update.component.html',
})
export class ConversationsUpdateComponent implements OnInit {
  isSaving = false;
  conversations: IConversations | null = null;

  editForm: ConversationsFormGroup = this.conversationsFormService.createConversationsFormGroup();

  constructor(
    protected conversationsService: ConversationsService,
    protected conversationsFormService: ConversationsFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conversations }) => {
      this.conversations = conversations;
      if (conversations) {
        this.updateForm(conversations);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const conversations = this.conversationsFormService.getConversations(this.editForm);
    if (conversations.conversationId !== null) {
      this.subscribeToSaveResponse(this.conversationsService.update(conversations));
    } else {
      this.subscribeToSaveResponse(this.conversationsService.create(conversations));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConversations>>): void {
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

  protected updateForm(conversations: IConversations): void {
    this.conversations = conversations;
    this.conversationsFormService.resetForm(this.editForm, conversations);
  }
}
