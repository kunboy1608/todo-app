import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ExpensesService } from '../service/expenses.service';

import { ExpensesComponent } from './expenses.component';
import SpyInstance = jest.SpyInstance;

describe('Expenses Management Component', () => {
  let comp: ExpensesComponent;
  let fixture: ComponentFixture<ExpensesComponent>;
  let service: ExpensesService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'expenses', component: ExpensesComponent }]), HttpClientTestingModule],
      declarations: [ExpensesComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'expenseId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'expenseId,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ExpensesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExpensesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExpensesService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ expenseId: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.expenses?.[0]).toEqual(expect.objectContaining({ expenseId: 123 }));
  });

  describe('trackExpenseId', () => {
    it('Should forward to expensesService', () => {
      const entity = { expenseId: 123 };
      jest.spyOn(service, 'getExpensesIdentifier');
      const expenseId = comp.trackExpenseId(0, entity);
      expect(service.getExpensesIdentifier).toHaveBeenCalledWith(entity);
      expect(expenseId).toBe(entity.expenseId);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['expenseId,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.navigateToWithComponentValues();

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      })
    );
  });
});
