import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ConversationsService } from '../service/conversations.service';

import { ConversationsComponent } from './conversations.component';
import SpyInstance = jest.SpyInstance;

describe('Conversations Management Component', () => {
  let comp: ConversationsComponent;
  let fixture: ComponentFixture<ConversationsComponent>;
  let service: ConversationsService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'conversations', component: ConversationsComponent }]), HttpClientTestingModule],
      declarations: [ConversationsComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'conversationId,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'conversationId,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ConversationsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ConversationsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ConversationsService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ conversationId: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.conversations?.[0]).toEqual(expect.objectContaining({ conversationId: 123 }));
  });

  describe('trackConversationId', () => {
    it('Should forward to conversationsService', () => {
      const entity = { conversationId: 123 };
      jest.spyOn(service, 'getConversationsIdentifier');
      const conversationId = comp.trackConversationId(0, entity);
      expect(service.getConversationsIdentifier).toHaveBeenCalledWith(entity);
      expect(conversationId).toBe(entity.conversationId);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['conversationId,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.navigateToWithComponentValues();

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      })
    );
  });
});
