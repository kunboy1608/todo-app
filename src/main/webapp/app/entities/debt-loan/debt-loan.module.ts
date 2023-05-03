import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DebtLoanComponent } from './list/debt-loan.component';
import { DebtLoanDetailComponent } from './detail/debt-loan-detail.component';
import { DebtLoanUpdateComponent } from './update/debt-loan-update.component';
import { DebtLoanDeleteDialogComponent } from './delete/debt-loan-delete-dialog.component';
import { DebtLoanRoutingModule } from './route/debt-loan-routing.module';

@NgModule({
  imports: [SharedModule, DebtLoanRoutingModule],
  declarations: [DebtLoanComponent, DebtLoanDetailComponent, DebtLoanUpdateComponent, DebtLoanDeleteDialogComponent],
})
export class DebtLoanModule {}
