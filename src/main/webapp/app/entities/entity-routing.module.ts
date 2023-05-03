import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'conversations',
        data: { pageTitle: 'gatewayApp.conversations.home.title' },
        loadChildren: () => import('./conversations/conversations.module').then(m => m.ConversationsModule),
      },
      {
        path: 'conversations-details',
        data: { pageTitle: 'gatewayApp.conversationsDetails.home.title' },
        loadChildren: () => import('./conversations-details/conversations-details.module').then(m => m.ConversationsDetailsModule),
      },
      {
        path: 'debt-loan',
        data: { pageTitle: 'gatewayApp.debtLoan.home.title' },
        loadChildren: () => import('./debt-loan/debt-loan.module').then(m => m.DebtLoanModule),
      },
      {
        path: 'events',
        data: { pageTitle: 'gatewayApp.events.home.title' },
        loadChildren: () => import('./events/events.module').then(m => m.EventsModule),
      },
      {
        path: 'expenses',
        data: { pageTitle: 'gatewayApp.expenses.home.title' },
        loadChildren: () => import('./expenses/expenses.module').then(m => m.ExpensesModule),
      },
      {
        path: 'notes',
        data: { pageTitle: 'gatewayApp.notes.home.title' },
        loadChildren: () => import('./notes/notes.module').then(m => m.NotesModule),
      },
      {
        path: 'profiles',
        data: { pageTitle: 'gatewayApp.profiles.home.title' },
        loadChildren: () => import('./profiles/profiles.module').then(m => m.ProfilesModule),
      },
      {
        path: 'relationship',
        data: { pageTitle: 'gatewayApp.relationship.home.title' },
        loadChildren: () => import('./relationship/relationship.module').then(m => m.RelationshipModule),
      },
      {
        path: 'tags',
        data: { pageTitle: 'gatewayApp.tags.home.title' },
        loadChildren: () => import('./tags/tags.module').then(m => m.TagsModule),
      },
      {
        path: 'types',
        data: { pageTitle: 'gatewayApp.types.home.title' },
        loadChildren: () => import('./types/types.module').then(m => m.TypesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
