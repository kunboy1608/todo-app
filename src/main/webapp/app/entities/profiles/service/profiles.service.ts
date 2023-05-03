import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProfiles, NewProfiles } from '../profiles.model';

export type PartialUpdateProfiles = Partial<IProfiles> & Pick<IProfiles, 'profileId'>;

type RestOf<T extends IProfiles | NewProfiles> = Omit<T, 'birthday' | 'createdOn' | 'modifiedOn'> & {
  birthday?: string | null;
  createdOn?: string | null;
  modifiedOn?: string | null;
};

export type RestProfiles = RestOf<IProfiles>;

export type NewRestProfiles = RestOf<NewProfiles>;

export type PartialUpdateRestProfiles = RestOf<PartialUpdateProfiles>;

export type EntityResponseType = HttpResponse<IProfiles>;
export type EntityArrayResponseType = HttpResponse<IProfiles[]>;

@Injectable({ providedIn: 'root' })
export class ProfilesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/profiles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(profiles: NewProfiles): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(profiles);
    return this.http
      .post<RestProfiles>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(profiles: IProfiles): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(profiles);
    return this.http
      .put<RestProfiles>(`${this.resourceUrl}/${this.getProfilesIdentifier(profiles)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(profiles: PartialUpdateProfiles): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(profiles);
    return this.http
      .patch<RestProfiles>(`${this.resourceUrl}/${this.getProfilesIdentifier(profiles)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProfiles>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProfiles[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getProfilesIdentifier(profiles: Pick<IProfiles, 'profileId'>): number {
    return profiles.profileId;
  }

  compareProfiles(o1: Pick<IProfiles, 'profileId'> | null, o2: Pick<IProfiles, 'profileId'> | null): boolean {
    return o1 && o2 ? this.getProfilesIdentifier(o1) === this.getProfilesIdentifier(o2) : o1 === o2;
  }

  addProfilesToCollectionIfMissing<Type extends Pick<IProfiles, 'profileId'>>(
    profilesCollection: Type[],
    ...profilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const profiles: Type[] = profilesToCheck.filter(isPresent);
    if (profiles.length > 0) {
      const profilesCollectionIdentifiers = profilesCollection.map(profilesItem => this.getProfilesIdentifier(profilesItem)!);
      const profilesToAdd = profiles.filter(profilesItem => {
        const profilesIdentifier = this.getProfilesIdentifier(profilesItem);
        if (profilesCollectionIdentifiers.includes(profilesIdentifier)) {
          return false;
        }
        profilesCollectionIdentifiers.push(profilesIdentifier);
        return true;
      });
      return [...profilesToAdd, ...profilesCollection];
    }
    return profilesCollection;
  }

  protected convertDateFromClient<T extends IProfiles | NewProfiles | PartialUpdateProfiles>(profiles: T): RestOf<T> {
    return {
      ...profiles,
      birthday: profiles.birthday?.format(DATE_FORMAT) ?? null,
      createdOn: profiles.createdOn?.toJSON() ?? null,
      modifiedOn: profiles.modifiedOn?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restProfiles: RestProfiles): IProfiles {
    return {
      ...restProfiles,
      birthday: restProfiles.birthday ? dayjs(restProfiles.birthday) : undefined,
      createdOn: restProfiles.createdOn ? dayjs(restProfiles.createdOn) : undefined,
      modifiedOn: restProfiles.modifiedOn ? dayjs(restProfiles.modifiedOn) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProfiles>): HttpResponse<IProfiles> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProfiles[]>): HttpResponse<IProfiles[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
