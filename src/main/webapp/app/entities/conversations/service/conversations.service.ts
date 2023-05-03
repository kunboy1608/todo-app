import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConversations, NewConversations } from '../conversations.model';

export type PartialUpdateConversations = Partial<IConversations> & Pick<IConversations, 'conversationId'>;

type RestOf<T extends IConversations | NewConversations> = Omit<T, 'timestamp'> & {
  timestamp?: string | null;
};

export type RestConversations = RestOf<IConversations>;

export type NewRestConversations = RestOf<NewConversations>;

export type PartialUpdateRestConversations = RestOf<PartialUpdateConversations>;

export type EntityResponseType = HttpResponse<IConversations>;
export type EntityArrayResponseType = HttpResponse<IConversations[]>;

@Injectable({ providedIn: 'root' })
export class ConversationsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conversations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(conversations: NewConversations): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversations);
    return this.http
      .post<RestConversations>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(conversations: IConversations): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversations);
    return this.http
      .put<RestConversations>(`${this.resourceUrl}/${this.getConversationsIdentifier(conversations)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(conversations: PartialUpdateConversations): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(conversations);
    return this.http
      .patch<RestConversations>(`${this.resourceUrl}/${this.getConversationsIdentifier(conversations)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestConversations>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestConversations[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getConversationsIdentifier(conversations: Pick<IConversations, 'conversationId'>): number {
    return conversations.conversationId;
  }

  compareConversations(o1: Pick<IConversations, 'conversationId'> | null, o2: Pick<IConversations, 'conversationId'> | null): boolean {
    return o1 && o2 ? this.getConversationsIdentifier(o1) === this.getConversationsIdentifier(o2) : o1 === o2;
  }

  addConversationsToCollectionIfMissing<Type extends Pick<IConversations, 'conversationId'>>(
    conversationsCollection: Type[],
    ...conversationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const conversations: Type[] = conversationsToCheck.filter(isPresent);
    if (conversations.length > 0) {
      const conversationsCollectionIdentifiers = conversationsCollection.map(
        conversationsItem => this.getConversationsIdentifier(conversationsItem)!
      );
      const conversationsToAdd = conversations.filter(conversationsItem => {
        const conversationsIdentifier = this.getConversationsIdentifier(conversationsItem);
        if (conversationsCollectionIdentifiers.includes(conversationsIdentifier)) {
          return false;
        }
        conversationsCollectionIdentifiers.push(conversationsIdentifier);
        return true;
      });
      return [...conversationsToAdd, ...conversationsCollection];
    }
    return conversationsCollection;
  }

  protected convertDateFromClient<T extends IConversations | NewConversations | PartialUpdateConversations>(conversations: T): RestOf<T> {
    return {
      ...conversations,
      timestamp: conversations.timestamp?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restConversations: RestConversations): IConversations {
    return {
      ...restConversations,
      timestamp: restConversations.timestamp ? dayjs(restConversations.timestamp) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestConversations>): HttpResponse<IConversations> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestConversations[]>): HttpResponse<IConversations[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
