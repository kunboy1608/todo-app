import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ConversationsDetailsFormService } from './conversations-details-form.service';
import { ConversationsDetailsService } from '../service/conversations-details.service';
import { IConversationsDetails } from '../conversations-details.model';
import { IConversations } from 'app/entities/conversations/conversations.model';
import { ConversationsService } from 'app/entities/conversations/service/conversations.service';

import { ConversationsDetailsUpdateComponent } from './conversations-details-update.component';

describe('ConversationsDetails Management Update Component', () => {
  let comp: ConversationsDetailsUpdateComponent;
  let fixture: ComponentFixture<ConversationsDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conversationsDetailsFormService: ConversationsDetailsFormService;
  let conversationsDetailsService: ConversationsDetailsService;
  let conversationsService: ConversationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ConversationsDetailsUpdateComponent],
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
      .overrideTemplate(ConversationsDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConversationsDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conversationsDetailsFormService = TestBed.inject(ConversationsDetailsFormService);
    conversationsDetailsService = TestBed.inject(ConversationsDetailsService);
    conversationsService = TestBed.inject(ConversationsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Conversations query and add missing value', () => {
      const conversationsDetails: IConversationsDetails = { id: 456 };
      const conversations: IConversations = { conversationId: 15834 };
      conversationsDetails.conversations = conversations;

      const conversationsCollection: IConversations[] = [{ conversationId: 90831 }];
      jest.spyOn(conversationsService, 'query').mockReturnValue(of(new HttpResponse({ body: conversationsCollection })));
      const additionalConversations = [conversations];
      const expectedCollection: IConversations[] = [...additionalConversations, ...conversationsCollection];
      jest.spyOn(conversationsService, 'addConversationsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ conversationsDetails });
      comp.ngOnInit();

      expect(conversationsService.query).toHaveBeenCalled();
      expect(conversationsService.addConversationsToCollectionIfMissing).toHaveBeenCalledWith(
        conversationsCollection,
        ...additionalConversations.map(expect.objectContaining)
      );
      expect(comp.conversationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const conversationsDetails: IConversationsDetails = { id: 456 };
      const conversations: IConversations = { conversationId: 17629 };
      conversationsDetails.conversations = conversations;

      activatedRoute.data = of({ conversationsDetails });
      comp.ngOnInit();

      expect(comp.conversationsSharedCollection).toContain(conversations);
      expect(comp.conversationsDetails).toEqual(conversationsDetails);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversationsDetails>>();
      const conversationsDetails = { id: 123 };
      jest.spyOn(conversationsDetailsFormService, 'getConversationsDetails').mockReturnValue(conversationsDetails);
      jest.spyOn(conversationsDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversationsDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversationsDetails }));
      saveSubject.complete();

      // THEN
      expect(conversationsDetailsFormService.getConversationsDetails).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conversationsDetailsService.update).toHaveBeenCalledWith(expect.objectContaining(conversationsDetails));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversationsDetails>>();
      const conversationsDetails = { id: 123 };
      jest.spyOn(conversationsDetailsFormService, 'getConversationsDetails').mockReturnValue({ id: null });
      jest.spyOn(conversationsDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversationsDetails: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversationsDetails }));
      saveSubject.complete();

      // THEN
      expect(conversationsDetailsFormService.getConversationsDetails).toHaveBeenCalled();
      expect(conversationsDetailsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversationsDetails>>();
      const conversationsDetails = { id: 123 };
      jest.spyOn(conversationsDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversationsDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conversationsDetailsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareConversations', () => {
      it('Should forward to conversationsService', () => {
        const entity = { conversationId: 123 };
        const entity2 = { conversationId: 456 };
        jest.spyOn(conversationsService, 'compareConversations');
        comp.compareConversations(entity, entity2);
        expect(conversationsService.compareConversations).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
