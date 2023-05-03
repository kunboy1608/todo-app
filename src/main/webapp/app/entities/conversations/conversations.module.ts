import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ConversationsComponent } from './list/conversations.component';
import { ConversationsDetailComponent } from './detail/conversations-detail.component';
import { ConversationsUpdateComponent } from './update/conversations-update.component';
import { ConversationsDeleteDialogComponent } from './delete/conversations-delete-dialog.component';
import { ConversationsRoutingModule } from './route/conversations-routing.module';

@NgModule({
  imports: [SharedModule, ConversationsRoutingModule],
  declarations: [ConversationsComponent, ConversationsDetailComponent, ConversationsUpdateComponent, ConversationsDeleteDialogComponent],
})
export class ConversationsModule {}
