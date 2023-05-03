import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TagsFormService } from './tags-form.service';
import { TagsService } from '../service/tags.service';
import { ITags } from '../tags.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { TagsUpdateComponent } from './tags-update.component';

describe('Tags Management Update Component', () => {
  let comp: TagsUpdateComponent;
  let fixture: ComponentFixture<TagsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tagsFormService: TagsFormService;
  let tagsService: TagsService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TagsUpdateComponent],
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
      .overrideTemplate(TagsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TagsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tagsFormService = TestBed.inject(TagsFormService);
    tagsService = TestBed.inject(TagsService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profiles query and add missing value', () => {
      const tags: ITags = { tagId: 456 };
      const profiles: IProfiles = { profileId: 13134 };
      tags.profiles = profiles;

      const profilesCollection: IProfiles[] = [{ profileId: 98569 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [profiles];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tags: ITags = { tagId: 456 };
      const profiles: IProfiles = { profileId: 53074 };
      tags.profiles = profiles;

      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(profiles);
      expect(comp.tags).toEqual(tags);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsFormService, 'getTags').mockReturnValue(tags);
      jest.spyOn(tagsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tags }));
      saveSubject.complete();

      // THEN
      expect(tagsFormService.getTags).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tagsService.update).toHaveBeenCalledWith(expect.objectContaining(tags));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsFormService, 'getTags').mockReturnValue({ tagId: null });
      jest.spyOn(tagsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tags }));
      saveSubject.complete();

      // THEN
      expect(tagsFormService.getTags).toHaveBeenCalled();
      expect(tagsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITags>>();
      const tags = { tagId: 123 };
      jest.spyOn(tagsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tags });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tagsService.update).toHaveBeenCalled();
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
