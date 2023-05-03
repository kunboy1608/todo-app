import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConversations } from '../conversations.model';

@Component({
  selector: 'jhi-conversations-detail',
  templateUrl: './conversations-detail.component.html',
})
export class ConversationsDetailComponent implements OnInit {
  conversations: IConversations | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conversations }) => {
      this.conversations = conversations;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
