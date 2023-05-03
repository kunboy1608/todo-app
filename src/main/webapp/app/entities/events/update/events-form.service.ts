import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEvents, NewEvents } from '../events.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { eventId: unknown }> = Partial<Omit<T, 'eventId'>> & { eventId: T['eventId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvents for edit and NewEventsFormGroupInput for create.
 */
type EventsFormGroupInput = IEvents | PartialWithRequiredKeyOf<NewEvents>;

type EventsFormDefaults = Pick<NewEvents, 'eventId' | 'isLunar'>;

type EventsFormGroupContent = {
  eventId: FormControl<IEvents['eventId'] | NewEvents['eventId']>;
  owner: FormControl<IEvents['owner']>;
  kind: FormControl<IEvents['kind']>;
  date: FormControl<IEvents['date']>;
  isLunar: FormControl<IEvents['isLunar']>;
  profiles: FormControl<IEvents['profiles']>;
};

export type EventsFormGroup = FormGroup<EventsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventsFormService {
  createEventsFormGroup(events: EventsFormGroupInput = { eventId: null }): EventsFormGroup {
    const eventsRawValue = {
      ...this.getFormDefaults(),
      ...events,
    };
    return new FormGroup<EventsFormGroupContent>({
      eventId: new FormControl(
        { value: eventsRawValue.eventId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      owner: new FormControl(eventsRawValue.owner),
      kind: new FormControl(eventsRawValue.kind),
      date: new FormControl(eventsRawValue.date),
      isLunar: new FormControl(eventsRawValue.isLunar),
      profiles: new FormControl(eventsRawValue.profiles),
    });
  }

  getEvents(form: EventsFormGroup): IEvents | NewEvents {
    return form.getRawValue() as IEvents | NewEvents;
  }

  resetForm(form: EventsFormGroup, events: EventsFormGroupInput): void {
    const eventsRawValue = { ...this.getFormDefaults(), ...events };
    form.reset(
      {
        ...eventsRawValue,
        eventId: { value: eventsRawValue.eventId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): EventsFormDefaults {
    return {
      eventId: null,
      isLunar: false,
    };
  }
}
