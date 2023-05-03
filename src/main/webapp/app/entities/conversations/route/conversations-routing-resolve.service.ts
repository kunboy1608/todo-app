import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConversations } from '../conversations.model';
import { ConversationsService } from '../service/conversations.service';

@Injectable({ providedIn: 'root' })
export class ConversationsRoutingResolveService implements Resolve<IConversations | null> {
  constructor(protected service: ConversationsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConversations | null | never> {
    const id = route.params['conversationId'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((conversations: HttpResponse<IConversations>) => {
          if (conversations.body) {
            return of(conversations.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
