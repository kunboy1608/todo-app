import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConversationsDetails, NewConversationsDetails } from '../conversations-details.model';

export type PartialUpdateConversationsDetails = Partial<IConversationsDetails> & Pick<IConversationsDetails, 'id'>;

type RestOf<T extends IConversationsDetails | NewConversationsDetails> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

export type RestConversationsDetails = RestOf<IConversationsDetails>;

export type NewRestConversationsDetails = RestOf<NewConversationsDetails>;

export type PartialUpdateRestConversationsDetails = RestOf<PartialUpdateConversationsDetails>;

export type EntityResponseType = HttpResponse<IConversationsDetails>;
export type EntityArrayResponseType = HttpResponse<IConversationsDetails[]>;

@Injectable({ providedIn: 'root' })
export class ConversationsDetailsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conversations-details');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(conversationsDetails: NewConversationsDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversationsDetails);
    return this.http
      .post<RestConversationsDetails>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(conversationsDetails: IConversationsDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversationsDetails);
    return this.http
      .put<RestConversationsDetails>(`${this.resourceUrl}/${this.getConversationsDetailsIdentifier(conversationsDetails)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(conversationsDetails: PartialUpdateConversationsDetails): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversationsDetails);
    return this.http
      .patch<RestConversationsDetails>(`${this.resourceUrl}/${this.getConversationsDetailsIdentifier(conversationsDetails)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestConversationsDetails>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestConversationsDetails[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getConversationsDetailsIdentifier(conversationsDetails: Pick<IConversationsDetails, 'id'>): number {
    return conversationsDetails.id;
  }

  compareConversationsDetails(o1: Pick<IConversationsDetails, 'id'> | null, o2: Pick<IConversationsDetails, 'id'> | null): boolean {
    return o1 && o2 ? this.getConversationsDetailsIdentifier(o1) === this.getConversationsDetailsIdentifier(o2) : o1 === o2;
  }

  addConversationsDetailsToCollectionIfMissing<Type extends Pick<IConversationsDetails, 'id'>>(
    conversationsDetailsCollection: Type[],
    ...conversationsDetailsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const conversationsDetails: Type[] = conversationsDetailsToCheck.filter(isPresent);
    if (conversationsDetails.length > 0) {
      const conversationsDetailsCollectionIdentifiers = conversationsDetailsCollection.map(
        conversationsDetailsItem => this.getConversationsDetailsIdentifier(conversationsDetailsItem)!
      );
      const conversationsDetailsToAdd = conversationsDetails.filter(conversationsDetailsItem => {
        const conversationsDetailsIdentifier = this.getConversationsDetailsIdentifier(conversationsDetailsItem);
        if (conversationsDetailsCollectionIdentifiers.includes(conversationsDetailsIdentifier)) {
          return false;
        }
        conversationsDetailsCollectionIdentifiers.push(conversationsDetailsIdentifier);
        return true;
      });
      return [...conversationsDetailsToAdd, ...conversationsDetailsCollection];
    }
    return conversationsDetailsCollection;
  }

  protected convertDateFromClient<T extends IConversationsDetails | NewConversationsDetails | PartialUpdateConversationsDetails>(
    conversationsDetails: T
  ): RestOf<T> {
    return {
      ...conversationsDetails,
      createdOn: conversationsDetails.createdOn?.toJSON() ?? null,
      modifiedOn: conversationsDetails.modifiedOn?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restConversationsDetails: RestConversationsDetails): IConversationsDetails {
    return {
      ...restConversationsDetails,
      createdOn: restConversationsDetails.createdOn ? dayjs(restConversationsDetails.createdOn) : undefined,
      modifiedOn: restConversationsDetails.modifiedOn ? dayjs(restConversationsDetails.modifiedOn) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestConversationsDetails>): HttpResponse<IConversationsDetails> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestConversationsDetails[]>): HttpResponse<IConversationsDetails[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
