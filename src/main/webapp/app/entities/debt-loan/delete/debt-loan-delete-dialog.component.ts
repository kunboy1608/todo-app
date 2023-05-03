import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDebtLoan } from '../debt-loan.model';
import { DebtLoanService } from '../service/debt-loan.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './debt-loan-delete-dialog.component.html',
})
export class DebtLoanDeleteDialogComponent {
  debtLoan?: IDebtLoan;

  constructor(protected debtLoanService: DebtLoanService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.debtLoanService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
