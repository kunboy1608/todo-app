import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ConversationsDetailsComponent } from '../list/conversations-details.component';
import { ConversationsDetailsDetailComponent } from '../detail/conversations-details-detail.component';
import { ConversationsDetailsUpdateComponent } from '../update/conversations-details-update.component';
import { ConversationsDetailsRoutingResolveService } from './conversations-details-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const conversationsDetailsRoute: Routes = [
  {
    path: '',
    component: ConversationsDetailsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ConversationsDetailsDetailComponent,
    resolve: {
      conversationsDetails: ConversationsDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ConversationsDetailsUpdateComponent,
    resolve: {
      conversationsDetails: ConversationsDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ConversationsDetailsUpdateComponent,
    resolve: {
      conversationsDetails: ConversationsDetailsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(conversationsDetailsRoute)],
  exports: [RouterModule],
})
export class ConversationsDetailsRoutingModule {}
