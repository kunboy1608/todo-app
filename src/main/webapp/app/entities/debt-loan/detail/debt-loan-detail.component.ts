import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDebtLoan } from '../debt-loan.model';

@Component({
  selector: 'jhi-debt-loan-detail',
  templateUrl: './debt-loan-detail.component.html',
})
export class DebtLoanDetailComponent implements OnInit {
  debtLoan: IDebtLoan | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ debtLoan }) => {
      this.debtLoan = debtLoan;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
