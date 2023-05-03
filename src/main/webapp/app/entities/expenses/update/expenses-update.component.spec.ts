import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ExpensesFormService } from './expenses-form.service';
import { ExpensesService } from '../service/expenses.service';
import { IExpenses } from '../expenses.model';
import { ITypes } from 'app/entities/types/types.model';
import { TypesService } from 'app/entities/types/service/types.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { ExpensesUpdateComponent } from './expenses-update.component';

describe('Expenses Management Update Component', () => {
  let comp: ExpensesUpdateComponent;
  let fixture: ComponentFixture<ExpensesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let expensesFormService: ExpensesFormService;
  let expensesService: ExpensesService;
  let typesService: TypesService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ExpensesUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ExpensesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpensesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    expensesFormService = TestBed.inject(ExpensesFormService);
    expensesService = TestBed.inject(ExpensesService);
    typesService = TestBed.inject(TypesService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Types query and add missing value', () => {
      const expenses: IExpenses = { expenseId: 456 };
      const types: ITypes = { typeId: 13013 };
      expenses.types = types;

      const typesCollection: ITypes[] = [{ typeId: 53131 }];
      jest.spyOn(typesService, 'query').mockReturnValue(of(new HttpResponse({ body: typesCollection })));
      const additionalTypes = [types];
      const expectedCollection: ITypes[] = [...additionalTypes, ...typesCollection];
      jest.spyOn(typesService, 'addTypesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ expenses });
      comp.ngOnInit();

      expect(typesService.query).toHaveBeenCalled();
      expect(typesService.addTypesToCollectionIfMissing).toHaveBeenCalledWith(
        typesCollection,
        ...additionalTypes.map(expect.objectContaining)
      );
      expect(comp.typesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Profiles query and add missing value', () => {
      const expenses: IExpenses = { expenseId: 456 };
      const profiles: IProfiles = { profileId: 29399 };
      expenses.profiles = profiles;

      const profilesCollection: IProfiles[] = [{ profileId: 39578 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [profiles];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ expenses });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const expenses: IExpenses = { expenseId: 456 };
      const types: ITypes = { typeId: 11376 };
      expenses.types = types;
      const profiles: IProfiles = { profileId: 86189 };
      expenses.profiles = profiles;

      activatedRoute.data = of({ expenses });
      comp.ngOnInit();

      expect(comp.typesSharedCollection).toContain(types);
      expect(comp.profilesSharedCollection).toContain(profiles);
      expect(comp.expenses).toEqual(expenses);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenses>>();
      const expenses = { expenseId: 123 };
      jest.spyOn(expensesFormService, 'getExpenses').mockReturnValue(expenses);
      jest.spyOn(expensesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expenses }));
      saveSubject.complete();

      // THEN
      expect(expensesFormService.getExpenses).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(expensesService.update).toHaveBeenCalledWith(expect.objectContaining(expenses));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenses>>();
      const expenses = { expenseId: 123 };
      jest.spyOn(expensesFormService, 'getExpenses').mockReturnValue({ expenseId: null });
      jest.spyOn(expensesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenses: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: expenses }));
      saveSubject.complete();

      // THEN
      expect(expensesFormService.getExpenses).toHaveBeenCalled();
      expect(expensesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IExpenses>>();
      const expenses = { expenseId: 123 };
      jest.spyOn(expensesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ expenses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(expensesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTypes', () => {
      it('Should forward to typesService', () => {
        const entity = { typeId: 123 };
        const entity2 = { typeId: 456 };
        jest.spyOn(typesService, 'compareTypes');
        comp.compareTypes(entity, entity2);
        expect(typesService.compareTypes).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProfiles', () => {
      it('Should forward to profilesService', () => {
        const entity = { profileId: 123 };
        const entity2 = { profileId: 456 };
        jest.spyOn(profilesService, 'compareProfiles');
        comp.compareProfiles(entity, entity2);
        expect(profilesService.compareProfiles).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
