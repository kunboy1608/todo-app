import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDebtLoan } from '../debt-loan.model';
import { DebtLoanService } from '../service/debt-loan.service';

@Injectable({ providedIn: 'root' })
export class DebtLoanRoutingResolveService implements Resolve<IDebtLoan | null> {
  constructor(protected service: DebtLoanService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDebtLoan | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((debtLoan: HttpResponse<IDebtLoan>) => {
          if (debtLoan.body) {
            return of(debtLoan.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
