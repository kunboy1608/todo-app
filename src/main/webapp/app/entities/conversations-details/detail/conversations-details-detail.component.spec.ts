import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ConversationsDetailsDetailComponent } from './conversations-details-detail.component';

describe('ConversationsDetails Management Detail Component', () => {
  let comp: ConversationsDetailsDetailComponent;
  let fixture: ComponentFixture<ConversationsDetailsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConversationsDetailsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ conversationsDetails: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ConversationsDetailsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ConversationsDetailsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load conversationsDetails on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.conversationsDetails).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
