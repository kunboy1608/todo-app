import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExpensesFormService, ExpensesFormGroup } from './expenses-form.service';
import { IExpenses } from '../expenses.model';
import { ExpensesService } from '../service/expenses.service';
import { ITypes } from 'app/entities/types/types.model';
import { TypesService } from 'app/entities/types/service/types.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-expenses-update',
  templateUrl: './expenses-update.component.html',
})
export class ExpensesUpdateComponent implements OnInit {
  isSaving = false;
  expenses: IExpenses | null = null;

  typesSharedCollection: ITypes[] = [];
  profilesSharedCollection: IProfiles[] = [];

  editForm: ExpensesFormGroup = this.expensesFormService.createExpensesFormGroup();

  constructor(
    protected expensesService: ExpensesService,
    protected expensesFormService: ExpensesFormService,
    protected typesService: TypesService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareTypes = (o1: ITypes | null, o2: ITypes | null): boolean => this.typesService.compareTypes(o1, o2);

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ expenses }) => {
      this.expenses = expenses;
      if (expenses) {
        this.updateForm(expenses);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const expenses = this.expensesFormService.getExpenses(this.editForm);
    if (expenses.expenseId !== null) {
      this.subscribeToSaveResponse(this.expensesService.update(expenses));
    } else {
      this.subscribeToSaveResponse(this.expensesService.create(expenses));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExpenses>>): void {
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

  protected updateForm(expenses: IExpenses): void {
    this.expenses = expenses;
    this.expensesFormService.resetForm(this.editForm, expenses);

    this.typesSharedCollection = this.typesService.addTypesToCollectionIfMissing<ITypes>(this.typesSharedCollection, expenses.types);
    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      expenses.profiles
    );
  }

  protected loadRelationshipsOptions(): void {
    this.typesService
      .query()
      .pipe(map((res: HttpResponse<ITypes[]>) => res.body ?? []))
      .pipe(map((types: ITypes[]) => this.typesService.addTypesToCollectionIfMissing<ITypes>(types, this.expenses?.types)))
      .subscribe((types: ITypes[]) => (this.typesSharedCollection = types));

    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfiles[]) => this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.expenses?.profiles))
      )
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
