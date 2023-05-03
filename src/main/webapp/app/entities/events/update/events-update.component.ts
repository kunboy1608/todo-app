import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EventsFormService, EventsFormGroup } from './events-form.service';
import { IEvents } from '../events.model';
import { EventsService } from '../service/events.service';
import { IProfiles } from 'app/entities/profiles/profiles.model';
import { ProfilesService } from 'app/entities/profiles/service/profiles.service';

@Component({
  selector: 'jhi-events-update',
  templateUrl: './events-update.component.html',
})
export class EventsUpdateComponent implements OnInit {
  isSaving = false;
  events: IEvents | null = null;

  profilesSharedCollection: IProfiles[] = [];

  editForm: EventsFormGroup = this.eventsFormService.createEventsFormGroup();

  constructor(
    protected eventsService: EventsService,
    protected eventsFormService: EventsFormService,
    protected profilesService: ProfilesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareProfiles = (o1: IProfiles | null, o2: IProfiles | null): boolean => this.profilesService.compareProfiles(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ events }) => {
      this.events = events;
      if (events) {
        this.updateForm(events);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const events = this.eventsFormService.getEvents(this.editForm);
    if (events.eventId !== null) {
      this.subscribeToSaveResponse(this.eventsService.update(events));
    } else {
      this.subscribeToSaveResponse(this.eventsService.create(events));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvents>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(events: IEvents): void {
    this.events = events;
    this.eventsFormService.resetForm(this.editForm, events);

    this.profilesSharedCollection = this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(
      this.profilesSharedCollection,
      events.profiles
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profilesService
      .query()
      .pipe(map((res: HttpResponse<IProfiles[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfiles[]) => this.profilesService.addProfilesToCollectionIfMissing<IProfiles>(profiles, this.events?.profiles))
      )
      .subscribe((profiles: IProfiles[]) => (this.profilesSharedCollection = profiles));
  }
}
