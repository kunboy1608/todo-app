import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ConversationsComponent } from '../list/conversations.component';
import { ConversationsDetailComponent } from '../detail/conversations-detail.component';
import { ConversationsUpdateComponent } from '../update/conversations-update.component';
import { ConversationsRoutingResolveService } from './conversations-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const conversationsRoute: Routes = [
  {
    path: '',
    component: ConversationsComponent,
    data: {
      defaultSort: 'conversationId,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':conversationId/view',
    component: ConversationsDetailComponent,
    resolve: {
      conversations: ConversationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ConversationsUpdateComponent,
    resolve: {
      conversations: ConversationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':conversationId/edit',
    component: ConversationsUpdateComponent,
    resolve: {
      conversations: ConversationsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(conversationsRoute)],
  exports: [RouterModule],
})
export class ConversationsRoutingModule {}
