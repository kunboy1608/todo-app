import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TypesFormService } from './types-form.service';
import { TypesService } from '../service/types.service';
import { ITypes } from '../types.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { TypesUpdateComponent } from './types-update.component';

describe('Types Management Update Component', () => {
  let comp: TypesUpdateComponent;
  let fixture: ComponentFixture<TypesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let typesFormService: TypesFormService;
  let typesService: TypesService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TypesUpdateComponent],
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
      .overrideTemplate(TypesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TypesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    typesFormService = TestBed.inject(TypesFormService);
    typesService = TestBed.inject(TypesService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profiles query and add missing value', () => {
      const types: ITypes = { typeId: 456 };
      const profiles: IProfiles = { profileId: 61133 };
      types.profiles = profiles;

      const profilesCollection: IProfiles[] = [{ profileId: 28882 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [profiles];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ types });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const types: ITypes = { typeId: 456 };
      const profiles: IProfiles = { profileId: 11823 };
      types.profiles = profiles;

      activatedRoute.data = of({ types });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(profiles);
      expect(comp.types).toEqual(types);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypes>>();
      const types = { typeId: 123 };
      jest.spyOn(typesFormService, 'getTypes').mockReturnValue(types);
      jest.spyOn(typesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ types });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: types }));
      saveSubject.complete();

      // THEN
      expect(typesFormService.getTypes).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(typesService.update).toHaveBeenCalledWith(expect.objectContaining(types));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypes>>();
      const types = { typeId: 123 };
      jest.spyOn(typesFormService, 'getTypes').mockReturnValue({ typeId: null });
      jest.spyOn(typesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ types: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: types }));
      saveSubject.complete();

      // THEN
      expect(typesFormService.getTypes).toHaveBeenCalled();
      expect(typesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITypes>>();
      const types = { typeId: 123 };
      jest.spyOn(typesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ types });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(typesService.update).toHaveBeenCalled();
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
