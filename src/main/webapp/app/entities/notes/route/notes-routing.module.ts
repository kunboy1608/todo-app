import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NotesComponent } from '../list/notes.component';
import { NotesDetailComponent } from '../detail/notes-detail.component';
import { NotesUpdateComponent } from '../update/notes-update.component';
import { NotesRoutingResolveService } from './notes-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const notesRoute: Routes = [
  {
    path: '',
    component: NotesComponent,
    data: {
      defaultSort: 'noteId,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':noteId/view',
    component: NotesDetailComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NotesUpdateComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':noteId/edit',
    component: NotesUpdateComponent,
    resolve: {
      notes: NotesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(notesRoute)],
  exports: [RouterModule],
})
export class NotesRoutingModule {}
