import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITypes, NewTypes } from '../types.model';

export type PartialUpdateTypes = Partial<ITypes> & Pick<ITypes, 'typeId'>;

type RestOf<T extends ITypes | NewTypes> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

export type RestTypes = RestOf<ITypes>;

export type NewRestTypes = RestOf<NewTypes>;

export type PartialUpdateRestTypes = RestOf<PartialUpdateTypes>;

export type EntityResponseType = HttpResponse<ITypes>;
export type EntityArrayResponseType = HttpResponse<ITypes[]>;

@Injectable({ providedIn: 'root' })
export class TypesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/types');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(types: NewTypes): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(types);
    return this.http.post<RestTypes>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(types: ITypes): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(types);
    return this.http
      .put<RestTypes>(`${this.resourceUrl}/${this.getTypesIdentifier(types)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(types: PartialUpdateTypes): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(types);
    return this.http
      .patch<RestTypes>(`${this.resourceUrl}/${this.getTypesIdentifier(types)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTypes>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTypes[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTypesIdentifier(types: Pick<ITypes, 'typeId'>): number {
    return types.typeId;
  }

  compareTypes(o1: Pick<ITypes, 'typeId'> | null, o2: Pick<ITypes, 'typeId'> | null): boolean {
    return o1 && o2 ? this.getTypesIdentifier(o1) === this.getTypesIdentifier(o2) : o1 === o2;
  }

  addTypesToCollectionIfMissing<Type extends Pick<ITypes, 'typeId'>>(
    typesCollection: Type[],
    ...typesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const types: Type[] = typesToCheck.filter(isPresent);
    if (types.length > 0) {
      const typesCollectionIdentifiers = typesCollection.map(typesItem => this.getTypesIdentifier(typesItem)!);
      const typesToAdd = types.filter(typesItem => {
        const typesIdentifier = this.getTypesIdentifier(typesItem);
        if (typesCollectionIdentifiers.includes(typesIdentifier)) {
          return false;
        }
        typesCollectionIdentifiers.push(typesIdentifier);
        return true;
      });
      return [...typesToAdd, ...typesCollection];
    }
    return typesCollection;
  }

  protected convertDateFromClient<T extends ITypes | NewTypes | PartialUpdateTypes>(types: T): RestOf<T> {
    return {
      ...types,
      createdOn: types.createdOn?.toJSON() ?? null,
      modifiedOn: types.modifiedOn?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTypes: RestTypes): ITypes {
    return {
      ...restTypes,
      createdOn: restTypes.createdOn ? dayjs(restTypes.createdOn) : undefined,
      modifiedOn: restTypes.modifiedOn ? dayjs(restTypes.modifiedOn) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTypes>): HttpResponse<ITypes> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTypes[]>): HttpResponse<ITypes[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
