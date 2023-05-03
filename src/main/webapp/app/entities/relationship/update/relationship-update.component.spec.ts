import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RelationshipFormService } from './relationship-form.service';
import { RelationshipService } from '../service/relationship.service';
import { IRelationship } from '../relationship.model';

import { RelationshipUpdateComponent } from './relationship-update.component';

describe('Relationship Management Update Component', () => {
  let comp: RelationshipUpdateComponent;
  let fixture: ComponentFixture<RelationshipUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let relationshipFormService: RelationshipFormService;
  let relationshipService: RelationshipService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RelationshipUpdateComponent],
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
      .overrideTemplate(RelationshipUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RelationshipUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    relationshipFormService = TestBed.inject(RelationshipFormService);
    relationshipService = TestBed.inject(RelationshipService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const relationship: IRelationship = { relationshipId: 456 };

      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      expect(comp.relationship).toEqual(relationship);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { relationshipId: 123 };
      jest.spyOn(relationshipFormService, 'getRelationship').mockReturnValue(relationship);
      jest.spyOn(relationshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationship }));
      saveSubject.complete();

      // THEN
      expect(relationshipFormService.getRelationship).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(relationshipService.update).toHaveBeenCalledWith(expect.objectContaining(relationship));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { relationshipId: 123 };
      jest.spyOn(relationshipFormService, 'getRelationship').mockReturnValue({ relationshipId: null });
      jest.spyOn(relationshipService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: relationship }));
      saveSubject.complete();

      // THEN
      expect(relationshipFormService.getRelationship).toHaveBeenCalled();
      expect(relationshipService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRelationship>>();
      const relationship = { relationshipId: 123 };
      jest.spyOn(relationshipService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ relationship });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(relationshipService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
