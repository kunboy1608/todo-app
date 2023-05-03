import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DebtLoanFormService } from './debt-loan-form.service';
import { DebtLoanService } from '../service/debt-loan.service';
import { IDebtLoan } from '../debt-loan.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { DebtLoanUpdateComponent } from './debt-loan-update.component';

describe('DebtLoan Management Update Component', () => {
  let comp: DebtLoanUpdateComponent;
  let fixture: ComponentFixture<DebtLoanUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let debtLoanFormService: DebtLoanFormService;
  let debtLoanService: DebtLoanService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DebtLoanUpdateComponent],
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
      .overrideTemplate(DebtLoanUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DebtLoanUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    debtLoanFormService = TestBed.inject(DebtLoanFormService);
    debtLoanService = TestBed.inject(DebtLoanService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profiles query and add missing value', () => {
      const debtLoan: IDebtLoan = { id: 456 };
      const debts: IProfiles = { profileId: 37705 };
      debtLoan.debts = debts;
      const loans: IProfiles = { profileId: 38802 };
      debtLoan.loans = loans;

      const profilesCollection: IProfiles[] = [{ profileId: 21371 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [debts, loans];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ debtLoan });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const debtLoan: IDebtLoan = { id: 456 };
      const debts: IProfiles = { profileId: 79816 };
      debtLoan.debts = debts;
      const loans: IProfiles = { profileId: 83920 };
      debtLoan.loans = loans;

      activatedRoute.data = of({ debtLoan });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(debts);
      expect(comp.profilesSharedCollection).toContain(loans);
      expect(comp.debtLoan).toEqual(debtLoan);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDebtLoan>>();
      const debtLoan = { id: 123 };
      jest.spyOn(debtLoanFormService, 'getDebtLoan').mockReturnValue(debtLoan);
      jest.spyOn(debtLoanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtLoan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: debtLoan }));
      saveSubject.complete();

      // THEN
      expect(debtLoanFormService.getDebtLoan).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(debtLoanService.update).toHaveBeenCalledWith(expect.objectContaining(debtLoan));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDebtLoan>>();
      const debtLoan = { id: 123 };
      jest.spyOn(debtLoanFormService, 'getDebtLoan').mockReturnValue({ id: null });
      jest.spyOn(debtLoanService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtLoan: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: debtLoan }));
      saveSubject.complete();

      // THEN
      expect(debtLoanFormService.getDebtLoan).toHaveBeenCalled();
      expect(debtLoanService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDebtLoan>>();
      const debtLoan = { id: 123 };
      jest.spyOn(debtLoanService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ debtLoan });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(debtLoanService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
