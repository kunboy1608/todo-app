import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProfilesService } from '../service/profiles.service';

import { ProfilesComponent } from './profiles.component';
import SpyInstance = jest.SpyInstance;

describe('Profiles Management Component', () => {
  let comp: ProfilesComponent;
  let fixture: ComponentFixture<ProfilesComponent>;
  let service: ProfilesService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'profiles', component: ProfilesComponent }]), HttpClientTestingModule],
      declarations: [ProfilesComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'profileId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'profileId,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ProfilesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfilesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProfilesService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ profileId: 123 }],
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
    expect(comp.profiles?.[0]).toEqual(expect.objectContaining({ profileId: 123 }));
  });

  describe('trackProfileId', () => {
    it('Should forward to profilesService', () => {
      const entity = { profileId: 123 };
      jest.spyOn(service, 'getProfilesIdentifier');
      const profileId = comp.trackProfileId(0, entity);
      expect(service.getProfilesIdentifier).toHaveBeenCalledWith(entity);
      expect(profileId).toBe(entity.profileId);
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['profileId,desc'] }));
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
