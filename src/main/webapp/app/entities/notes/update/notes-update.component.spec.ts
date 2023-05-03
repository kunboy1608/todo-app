import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotesFormService } from './notes-form.service';
import { NotesService } from '../service/notes.service';
import { INotes } from '../notes.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { NotesUpdateComponent } from './notes-update.component';

describe('Notes Management Update Component', () => {
  let comp: NotesUpdateComponent;
  let fixture: ComponentFixture<NotesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notesFormService: NotesFormService;
  let notesService: NotesService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotesUpdateComponent],
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
      .overrideTemplate(NotesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notesFormService = TestBed.inject(NotesFormService);
    notesService = TestBed.inject(NotesService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profiles query and add missing value', () => {
      const notes: INotes = { noteId: 456 };
      const profiles: IProfiles = { profileId: 29737 };
      notes.profiles = profiles;

      const profilesCollection: IProfiles[] = [{ profileId: 94418 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [profiles];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notes: INotes = { noteId: 456 };
      const profiles: IProfiles = { profileId: 38601 };
      notes.profiles = profiles;

      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(profiles);
      expect(comp.notes).toEqual(notes);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotes>>();
      const notes = { noteId: 123 };
      jest.spyOn(notesFormService, 'getNotes').mockReturnValue(notes);
      jest.spyOn(notesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notes }));
      saveSubject.complete();

      // THEN
      expect(notesFormService.getNotes).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notesService.update).toHaveBeenCalledWith(expect.objectContaining(notes));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotes>>();
      const notes = { noteId: 123 };
      jest.spyOn(notesFormService, 'getNotes').mockReturnValue({ noteId: null });
      jest.spyOn(notesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notes }));
      saveSubject.complete();

      // THEN
      expect(notesFormService.getNotes).toHaveBeenCalled();
      expect(notesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotes>>();
      const notes = { noteId: 123 };
      jest.spyOn(notesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notes });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notesService.update).toHaveBeenCalled();
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
