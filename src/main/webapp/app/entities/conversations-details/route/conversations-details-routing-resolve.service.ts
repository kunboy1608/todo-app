import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IConversationsDetails } from '../conversations-details.model';
import { ConversationsDetailsService } from '../service/conversations-details.service';

@Injectable({ providedIn: 'root' })
export class ConversationsDetailsRoutingResolveService implements Resolve<IConversationsDetails | null> {
  constructor(protected service: ConversationsDetailsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IConversationsDetails | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((conversationsDetails: HttpResponse<IConversationsDetails>) => {
          if (conversationsDetails.body) {
            return of(conversationsDetails.body);
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
