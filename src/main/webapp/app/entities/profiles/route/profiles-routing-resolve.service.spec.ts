import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IProfiles } from '../profiles.model';
import { ProfilesService } from '../service/profiles.service';

import { ProfilesRoutingResolveService } from './profiles-routing-resolve.service';

describe('Profiles routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: ProfilesRoutingResolveService;
  let service: ProfilesService;
  let resultProfiles: IProfiles | null | undefined;

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
    routingResolveService = TestBed.inject(ProfilesRoutingResolveService);
    service = TestBed.inject(ProfilesService);
    resultProfiles = undefined;
  });

  describe('resolve', () => {
    it('should return IProfiles returned by find', () => {
      // GIVEN
      service.find = jest.fn(profileId => of(new HttpResponse({ body: { profileId } })));
      mockActivatedRouteSnapshot.params = { profileId: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfiles = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProfiles).toEqual({ profileId: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfiles = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultProfiles).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IProfiles>({ body: null })));
      mockActivatedRouteSnapshot.params = { profileId: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultProfiles = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultProfiles).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
