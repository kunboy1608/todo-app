import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NotesDetailComponent } from './notes-detail.component';

describe('Notes Management Detail Component', () => {
  let comp: NotesDetailComponent;
  let fixture: ComponentFixture<NotesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NotesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ notes: { noteId: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NotesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NotesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load notes on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.notes).toEqual(expect.objectContaining({ noteId: 123 }));
    });
  });
});
