import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INotes, NewNotes } from '../notes.model';

export type PartialUpdateNotes = Partial<INotes> & Pick<INotes, 'noteId'>;

export type EntityResponseType = HttpResponse<INotes>;
export type EntityArrayResponseType = HttpResponse<INotes[]>;

@Injectable({ providedIn: 'root' })
export class NotesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(notes: NewNotes): Observable<EntityResponseType> {
    return this.http.post<INotes>(this.resourceUrl, notes, { observe: 'response' });
  }

  update(notes: INotes): Observable<EntityResponseType> {
    return this.http.put<INotes>(`${this.resourceUrl}/${this.getNotesIdentifier(notes)}`, notes, { observe: 'response' });
  }

  partialUpdate(notes: PartialUpdateNotes): Observable<EntityResponseType> {
    return this.http.patch<INotes>(`${this.resourceUrl}/${this.getNotesIdentifier(notes)}`, notes, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INotes>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INotes[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getNotesIdentifier(notes: Pick<INotes, 'noteId'>): number {
    return notes.noteId;
  }

  compareNotes(o1: Pick<INotes, 'noteId'> | null, o2: Pick<INotes, 'noteId'> | null): boolean {
    return o1 && o2 ? this.getNotesIdentifier(o1) === this.getNotesIdentifier(o2) : o1 === o2;
  }

  addNotesToCollectionIfMissing<Type extends Pick<INotes, 'noteId'>>(
    notesCollection: Type[],
    ...notesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const notes: Type[] = notesToCheck.filter(isPresent);
    if (notes.length > 0) {
      const notesCollectionIdentifiers = notesCollection.map(notesItem => this.getNotesIdentifier(notesItem)!);
      const notesToAdd = notes.filter(notesItem => {
        const notesIdentifier = this.getNotesIdentifier(notesItem);
        if (notesCollectionIdentifiers.includes(notesIdentifier)) {
          return false;
        }
        notesCollectionIdentifiers.push(notesIdentifier);
        return true;
      });
      return [...notesToAdd, ...notesCollection];
    }
    return notesCollection;
  }
}
