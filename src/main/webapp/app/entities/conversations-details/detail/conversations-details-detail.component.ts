import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConversationsDetails } from '../conversations-details.model';

@Component({
  selector: 'jhi-conversations-details-detail',
  templateUrl: './conversations-details-detail.component.html',
})
export class ConversationsDetailsDetailComponent implements OnInit {
  conversationsDetails: IConversationsDetails | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ conversationsDetails }) => {
      this.conversationsDetails = conversationsDetails;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
