import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDebtLoan, NewDebtLoan } from '../debt-loan.model';

export type PartialUpdateDebtLoan = Partial<IDebtLoan> & Pick<IDebtLoan, 'id'>;

type RestOf<T extends IDebtLoan | NewDebtLoan> = Omit<T, 'deadline' | 'datOfPayment'> & {
  deadline?: string | null;
  datOfPayment?: string | null;
};

export type RestDebtLoan = RestOf<IDebtLoan>;

export type NewRestDebtLoan = RestOf<NewDebtLoan>;

export type PartialUpdateRestDebtLoan = RestOf<PartialUpdateDebtLoan>;

export type EntityResponseType = HttpResponse<IDebtLoan>;
export type EntityArrayResponseType = HttpResponse<IDebtLoan[]>;

@Injectable({ providedIn: 'root' })
export class DebtLoanService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/debt-loans');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(debtLoan: NewDebtLoan): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(debtLoan);
    return this.http
      .post<RestDebtLoan>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(debtLoan: IDebtLoan): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(debtLoan);
    return this.http
      .put<RestDebtLoan>(`${this.resourceUrl}/${this.getDebtLoanIdentifier(debtLoan)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(debtLoan: PartialUpdateDebtLoan): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(debtLoan);
    return this.http
      .patch<RestDebtLoan>(`${this.resourceUrl}/${this.getDebtLoanIdentifier(debtLoan)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDebtLoan>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDebtLoan[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDebtLoanIdentifier(debtLoan: Pick<IDebtLoan, 'id'>): number {
    return debtLoan.id;
  }

  compareDebtLoan(o1: Pick<IDebtLoan, 'id'> | null, o2: Pick<IDebtLoan, 'id'> | null): boolean {
    return o1 && o2 ? this.getDebtLoanIdentifier(o1) === this.getDebtLoanIdentifier(o2) : o1 === o2;
  }

  addDebtLoanToCollectionIfMissing<Type extends Pick<IDebtLoan, 'id'>>(
    debtLoanCollection: Type[],
    ...debtLoansToCheck: (Type | null | undefined)[]
  ): Type[] {
    const debtLoans: Type[] = debtLoansToCheck.filter(isPresent);
    if (debtLoans.length > 0) {
      const debtLoanCollectionIdentifiers = debtLoanCollection.map(debtLoanItem => this.getDebtLoanIdentifier(debtLoanItem)!);
      const debtLoansToAdd = debtLoans.filter(debtLoanItem => {
        const debtLoanIdentifier = this.getDebtLoanIdentifier(debtLoanItem);
        if (debtLoanCollectionIdentifiers.includes(debtLoanIdentifier)) {
          return false;
        }
        debtLoanCollectionIdentifiers.push(debtLoanIdentifier);
        return true;
      });
      return [...debtLoansToAdd, ...debtLoanCollection];
    }
    return debtLoanCollection;
  }

  protected convertDateFromClient<T extends IDebtLoan | NewDebtLoan | PartialUpdateDebtLoan>(debtLoan: T): RestOf<T> {
    return {
      ...debtLoan,
      deadline: debtLoan.deadline?.toJSON() ?? null,
      datOfPayment: debtLoan.datOfPayment?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDebtLoan: RestDebtLoan): IDebtLoan {
    return {
      ...restDebtLoan,
      deadline: restDebtLoan.deadline ? dayjs(restDebtLoan.deadline) : undefined,
      datOfPayment: restDebtLoan.datOfPayment ? dayjs(restDebtLoan.datOfPayment) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDebtLoan>): HttpResponse<IDebtLoan> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDebtLoan[]>): HttpResponse<IDebtLoan[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
