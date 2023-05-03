import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProfiles } from '../profiles.model';
import { ProfilesService } from '../service/profiles.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './profiles-delete-dialog.component.html',
})
export class ProfilesDeleteDialogComponent {
  profiles?: IProfiles;

  constructor(protected profilesService: ProfilesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.profilesService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
