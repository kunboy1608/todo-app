import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProfilesFormService } from './profiles-form.service';
import { ProfilesService } from '../service/profiles.service';
import { IProfiles } from '../profiles.model';

import { ProfilesUpdateComponent } from './profiles-update.component';

describe('Profiles Management Update Component', () => {
  let comp: ProfilesUpdateComponent;
  let fixture: ComponentFixture<ProfilesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let profilesFormService: ProfilesFormService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProfilesUpdateComponent],
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
      .overrideTemplate(ProfilesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfilesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    profilesFormService = TestBed.inject(ProfilesFormService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const profiles: IProfiles = { profileId: 456 };

      activatedRoute.data = of({ profiles });
      comp.ngOnInit();

      expect(comp.profiles).toEqual(profiles);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfiles>>();
      const profiles = { profileId: 123 };
      jest.spyOn(profilesFormService, 'getProfiles').mockReturnValue(profiles);
      jest.spyOn(profilesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profiles });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profiles }));
      saveSubject.complete();

      // THEN
      expect(profilesFormService.getProfiles).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(profilesService.update).toHaveBeenCalledWith(expect.objectContaining(profiles));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfiles>>();
      const profiles = { profileId: 123 };
      jest.spyOn(profilesFormService, 'getProfiles').mockReturnValue({ profileId: null });
      jest.spyOn(profilesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profiles: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: profiles }));
      saveSubject.complete();

      // THEN
      expect(profilesFormService.getProfiles).toHaveBeenCalled();
      expect(profilesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProfiles>>();
      const profiles = { profileId: 123 };
      jest.spyOn(profilesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ profiles });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(profilesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
