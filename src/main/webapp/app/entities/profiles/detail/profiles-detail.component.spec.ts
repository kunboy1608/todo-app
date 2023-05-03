import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProfilesDetailComponent } from './profiles-detail.component';

describe('Profiles Management Detail Component', () => {
  let comp: ProfilesDetailComponent;
  let fixture: ComponentFixture<ProfilesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfilesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ profiles: { profileId: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProfilesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProfilesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load profiles on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.profiles).toEqual(expect.objectContaining({ profileId: 123 }));
    });
  });
});
