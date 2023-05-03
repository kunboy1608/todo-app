import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITags } from '../tags.model';
import { TagsService } from '../service/tags.service';

import { TagsRoutingResolveService } from './tags-routing-resolve.service';

describe('Tags routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TagsRoutingResolveService;
  let service: TagsService;
  let resultTags: ITags | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(TagsRoutingResolveService);
    service = TestBed.inject(TagsService);
    resultTags = undefined;
  });

  describe('resolve', () => {
    it('should return ITags returned by find', () => {
      // GIVEN
      service.find = jest.fn(tagId => of(new HttpResponse({ body: { tagId } })));
      mockActivatedRouteSnapshot.params = { tagId: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTags = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTags).toEqual({ tagId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTags = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTags).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITags>({ body: null })));
      mockActivatedRouteSnapshot.params = { tagId: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTags = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTags).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
