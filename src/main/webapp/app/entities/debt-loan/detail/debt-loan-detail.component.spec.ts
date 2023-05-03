import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DebtLoanDetailComponent } from './debt-loan-detail.component';

describe('DebtLoan Management Detail Component', () => {
  let comp: DebtLoanDetailComponent;
  let fixture: ComponentFixture<DebtLoanDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DebtLoanDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ debtLoan: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DebtLoanDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DebtLoanDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load debtLoan on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.debtLoan).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
