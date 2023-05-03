import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RelationshipService } from '../service/relationship.service';

import { RelationshipComponent } from './relationship.component';
import SpyInstance = jest.SpyInstance;

describe('Relationship Management Component', () => {
  let comp: RelationshipComponent;
  let fixture: ComponentFixture<RelationshipComponent>;
  let service: RelationshipService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'relationship', component: RelationshipComponent }]), HttpClientTestingModule],
      declarations: [RelationshipComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'relationshipId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'relationshipId,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(RelationshipComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RelationshipService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ relationshipId: 123 }],
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
    expect(comp.relationships?.[0]).toEqual(expect.objectContaining({ relationshipId: 123 }));
  });

  describe('trackRelationshipId', () => {
    it('Should forward to relationshipService', () => {
      const entity = { relationshipId: 123 };
      jest.spyOn(service, 'getRelationshipIdentifier');
      const relationshipId = comp.trackRelationshipId(0, entity);
      expect(service.getRelationshipIdentifier).toHaveBeenCalledWith(entity);
      expect(relationshipId).toBe(entity.relationshipId);
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['relationshipId,desc'] }));
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
