import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EventsFormService } from './events-form.service';
import { EventsService } from '../service/events.service';
import { IEvents } from '../events.model';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

import { EventsUpdateComponent } from './events-update.component';

describe('Events Management Update Component', () => {
  let comp: EventsUpdateComponent;
  let fixture: ComponentFixture<EventsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventsFormService: EventsFormService;
  let eventsService: EventsService;
  let profilesService: ProfilesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EventsUpdateComponent],
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
      .overrideTemplate(EventsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventsFormService = TestBed.inject(EventsFormService);
    eventsService = TestBed.inject(EventsService);
    profilesService = TestBed.inject(ProfilesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Profiles query and add missing value', () => {
      const events: IEvents = { eventId: 456 };
      const profiles: IProfiles = { profileId: 12354 };
      events.profiles = profiles;

      const profilesCollection: IProfiles[] = [{ profileId: 61247 }];
      jest.spyOn(profilesService, 'query').mockReturnValue(of(new HttpResponse({ body: profilesCollection })));
      const additionalProfiles = [profiles];
      const expectedCollection: IProfiles[] = [...additionalProfiles, ...profilesCollection];
      jest.spyOn(profilesService, 'addProfilesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ events });
      comp.ngOnInit();

      expect(profilesService.query).toHaveBeenCalled();
      expect(profilesService.addProfilesToCollectionIfMissing).toHaveBeenCalledWith(
        profilesCollection,
        ...additionalProfiles.map(expect.objectContaining)
      );
      expect(comp.profilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const events: IEvents = { eventId: 456 };
      const profiles: IProfiles = { profileId: 61859 };
      events.profiles = profiles;

      activatedRoute.data = of({ events });
      comp.ngOnInit();

      expect(comp.profilesSharedCollection).toContain(profiles);
      expect(comp.events).toEqual(events);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { eventId: 123 };
      jest.spyOn(eventsFormService, 'getEvents').mockReturnValue(events);
      jest.spyOn(eventsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: events }));
      saveSubject.complete();

      // THEN
      expect(eventsFormService.getEvents).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventsService.update).toHaveBeenCalledWith(expect.objectContaining(events));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { eventId: 123 };
      jest.spyOn(eventsFormService, 'getEvents').mockReturnValue({ eventId: null });
      jest.spyOn(eventsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: events }));
      saveSubject.complete();

      // THEN
      expect(eventsFormService.getEvents).toHaveBeenCalled();
      expect(eventsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEvents>>();
      const events = { eventId: 123 };
      jest.spyOn(eventsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ events });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventsService.update).toHaveBeenCalled();
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
