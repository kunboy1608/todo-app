import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DebtLoanFormService, DebtLoanFormGroup } from './debt-loan-form.service';
import { IDebtLoan } from '../debt-loan.model';
import { DebtLoanService } from '../service/debt-loan.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-debt-loan-update',
  templateUrl: './debt-loan-update.component.html',
})
export class DebtLoanUpdateComponent implements OnInit {
  isSaving = false;
  debtLoan: IDebtLoan | null = null;

  profilesSharedCollection: IProfiles[] = [];

  editForm: DebtLoanFormGroup = this.debtLoanFormService.createDebtLoanFormGroup();

  constructor(
    protected debtLoanService: DebtLoanService,
    protected debtLoanFormService: DebtLoanFormService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ debtLoan }) => {
      this.debtLoan = debtLoan;
      if (debtLoan) {
        this.updateForm(debtLoan);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const debtLoan = this.debtLoanFormService.getDebtLoan(this.editForm);
    if (debtLoan.id !== null) {
      this.subscribeToSaveResponse(this.debtLoanService.update(debtLoan));
    } else {
      this.subscribeToSaveResponse(this.debtLoanService.create(debtLoan));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDebtLoan>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(debtLoan: IDebtLoan): void {
    this.debtLoan = debtLoan;
    this.debtLoanFormService.resetForm(this.editForm, debtLoan);

    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      debtLoan.debts,
      debtLoan.loans
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfiles[]) =>
          this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.debtLoan?.debts, this.debtLoan?.loans)
        )
      )
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
