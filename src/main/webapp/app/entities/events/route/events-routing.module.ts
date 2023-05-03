import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EventsComponent } from '../list/events.component';
import { EventsDetailComponent } from '../detail/events-detail.component';
import { EventsUpdateComponent } from '../update/events-update.component';
import { EventsRoutingResolveService } from './events-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const eventsRoute: Routes = [
  {
    path: '',
    component: EventsComponent,
    data: {
      defaultSort: 'eventId,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':eventId/view',
    component: EventsDetailComponent,
    resolve: {
      events: EventsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EventsUpdateComponent,
    resolve: {
      events: EventsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':eventId/edit',
    component: EventsUpdateComponent,
    resolve: {
      events: EventsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(eventsRoute)],
  exports: [RouterModule],
})
export class EventsRoutingModule {}
