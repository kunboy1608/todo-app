import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ConversationsDetailsComponent } from './list/conversations-details.component';
import { ConversationsDetailsDetailComponent } from './detail/conversations-details-detail.component';
import { ConversationsDetailsUpdateComponent } from './update/conversations-details-update.component';
import { ConversationsDetailsDeleteDialogComponent } from './delete/conversations-details-delete-dialog.component';
import { ConversationsDetailsRoutingModule } from './route/conversations-details-routing.module';

@NgModule({
  imports: [SharedModule, ConversationsDetailsRoutingModule],
  declarations: [
    ConversationsDetailsComponent,
    ConversationsDetailsDetailComponent,
    ConversationsDetailsUpdateComponent,
    ConversationsDetailsDeleteDialogComponent,
  ],
})
export class ConversationsDetailsModule {}
