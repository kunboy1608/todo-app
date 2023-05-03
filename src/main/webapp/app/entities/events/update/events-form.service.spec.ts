import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../events.test-samples';

import { EventsFormService } from './events-form.service';

describe('Events Form Service', () => {
  let service: EventsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventsFormService);
  });

  describe('Service methods', () => {
    describe('createEventsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEventsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            eventId: expect.any(Object),
            owner: expect.any(Object),
            kind: expect.any(Object),
            date: expect.any(Object),
            isLunar: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });

      it('passing IEvents should create a new form with FormGroup', () => {
        const formGroup = service.createEventsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            eventId: expect.any(Object),
            owner: expect.any(Object),
            kind: expect.any(Object),
            date: expect.any(Object),
            isLunar: expect.any(Object),
            profiles: expect.any(Object),
          })
        );
      });
    });

    describe('getEvents', () => {
      it('should return NewEvents for default Events initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createEventsFormGroup(sampleWithNewData);

        const events = service.getEvents(formGroup) as any;

        expect(events).toMatchObject(sampleWithNewData);
      });

      it('should return NewEvents for empty Events initial value', () => {
        const formGroup = service.createEventsFormGroup();

        const events = service.getEvents(formGroup) as any;

        expect(events).toMatchObject({});
      });

      it('should return IEvents', () => {
        const formGroup = service.createEventsFormGroup(sampleWithRequiredData);

        const events = service.getEvents(formGroup) as any;

        expect(events).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEvents should not enable eventId FormControl', () => {
        const formGroup = service.createEventsFormGroup();
        expect(formGroup.controls.eventId.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.eventId.disabled).toBe(true);
      });

      it('passing NewEvents should disable eventId FormControl', () => {
        const formGroup = service.createEventsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.eventId.disabled).toBe(true);

        service.resetForm(formGroup, { eventId: null });

        expect(formGroup.controls.eventId.disabled).toBe(true);
      });
    });
  });
});
