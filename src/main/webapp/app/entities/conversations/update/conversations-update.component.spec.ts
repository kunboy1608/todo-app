import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ConversationsFormService } from './conversations-form.service';
import { ConversationsService } from '../service/conversations.service';
import { IConversations } from '../conversations.model';

import { ConversationsUpdateComponent } from './conversations-update.component';

describe('Conversations Management Update Component', () => {
  let comp: ConversationsUpdateComponent;
  let fixture: ComponentFixture<ConversationsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let conversationsFormService: ConversationsFormService;
  let conversationsService: ConversationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ConversationsUpdateComponent],
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
      .overrideTemplate(ConversationsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConversationsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    conversationsFormService = TestBed.inject(ConversationsFormService);
    conversationsService = TestBed.inject(ConversationsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const conversations: IConversations = { conversationId: 456 };

      activatedRoute.data = of({ conversations });
      comp.ngOnInit();

      expect(comp.conversations).toEqual(conversations);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversations>>();
      const conversations = { conversationId: 123 };
      jest.spyOn(conversationsFormService, 'getConversations').mockReturnValue(conversations);
      jest.spyOn(conversationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversations });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversations }));
      saveSubject.complete();

      // THEN
      expect(conversationsFormService.getConversations).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(conversationsService.update).toHaveBeenCalledWith(expect.objectContaining(conversations));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversations>>();
      const conversations = { conversationId: 123 };
      jest.spyOn(conversationsFormService, 'getConversations').mockReturnValue({ conversationId: null });
      jest.spyOn(conversationsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversations: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: conversations }));
      saveSubject.complete();

      // THEN
      expect(conversationsFormService.getConversations).toHaveBeenCalled();
      expect(conversationsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IConversations>>();
      const conversations = { conversationId: 123 };
      jest.spyOn(conversationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ conversations });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(conversationsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
