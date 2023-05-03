import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DebtLoanComponent } from '../list/debt-loan.component';
import { DebtLoanDetailComponent } from '../detail/debt-loan-detail.component';
import { DebtLoanUpdateComponent } from '../update/debt-loan-update.component';
import { DebtLoanRoutingResolveService } from './debt-loan-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const debtLoanRoute: Routes = [
  {
    path: '',
    component: DebtLoanComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DebtLoanDetailComponent,
    resolve: {
      debtLoan: DebtLoanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DebtLoanUpdateComponent,
    resolve: {
      debtLoan: DebtLoanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DebtLoanUpdateComponent,
    resolve: {
      debtLoan: DebtLoanRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(debtLoanRoute)],
  exports: [RouterModule],
})
export class DebtLoanRoutingModule {}
