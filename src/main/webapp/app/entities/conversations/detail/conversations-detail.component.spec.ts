import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ConversationsDetailComponent } from './conversations-detail.component';

describe('Conversations Management Detail Component', () => {
  let comp: ConversationsDetailComponent;
  let fixture: ComponentFixture<ConversationsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConversationsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ conversations: { conversationId: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ConversationsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ConversationsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load conversations on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.conversations).toEqual(expect.objectContaining({ conversationId: 123 }));
    });
  });
});
