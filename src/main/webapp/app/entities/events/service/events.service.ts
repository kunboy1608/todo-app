import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvents, NewEvents } from '../events.model';

export type PartialUpdateEvents = Partial<IEvents> & Pick<IEvents, 'eventId'>;

export type EntityResponseType = HttpResponse<IEvents>;
export type EntityArrayResponseType = HttpResponse<IEvents[]>;

@Injectable({ providedIn: 'root' })
export class EventsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/events');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(events: NewEvents): Observable<EntityResponseType> {
    return this.http.post<IEvents>(this.resourceUrl, events, { observe: 'response' });
  }

  update(events: IEvents): Observable<EntityResponseType> {
    return this.http.put<IEvents>(`${this.resourceUrl}/${this.getEventsIdentifier(events)}`, events, { observe: 'response' });
  }

  partialUpdate(events: PartialUpdateEvents): Observable<EntityResponseType> {
    return this.http.patch<IEvents>(`${this.resourceUrl}/${this.getEventsIdentifier(events)}`, events, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IEvents>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IEvents[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEventsIdentifier(events: Pick<IEvents, 'eventId'>): number {
    return events.eventId;
  }

  compareEvents(o1: Pick<IEvents, 'eventId'> | null, o2: Pick<IEvents, 'eventId'> | null): boolean {
    return o1 && o2 ? this.getEventsIdentifier(o1) === this.getEventsIdentifier(o2) : o1 === o2;
  }

  addEventsToCollectionIfMissing<Type extends Pick<IEvents, 'eventId'>>(
    eventsCollection: Type[],
    ...eventsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const events: Type[] = eventsToCheck.filter(isPresent);
    if (events.length > 0) {
      const eventsCollectionIdentifiers = eventsCollection.map(eventsItem => this.getEventsIdentifier(eventsItem)!);
      const eventsToAdd = events.filter(eventsItem => {
        const eventsIdentifier = this.getEventsIdentifier(eventsItem);
        if (eventsCollectionIdentifiers.includes(eventsIdentifier)) {
          return false;
        }
        eventsCollectionIdentifiers.push(eventsIdentifier);
        return true;
      });
      return [...eventsToAdd, ...eventsCollection];
    }
    return eventsCollection;
  }
}
