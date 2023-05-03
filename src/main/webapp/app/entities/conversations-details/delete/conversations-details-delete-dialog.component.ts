import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IConversationsDetails } from '../conversations-details.model';
import { ConversationsDetailsService } from '../service/conversations-details.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './conversations-details-delete-dialog.component.html',
})
export class ConversationsDetailsDeleteDialogComponent {
  conversationsDetails?: IConversationsDetails;

  constructor(protected conversationsDetailsService: ConversationsDetailsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.conversationsDetailsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
