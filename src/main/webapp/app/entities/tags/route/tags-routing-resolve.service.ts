import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITags } from '../tags.model';
import { TagsService } from '../service/tags.service';

@Injectable({ providedIn: 'root' })
export class TagsRoutingResolveService implements Resolve<ITags | null> {
  constructor(protected service: TagsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITags | null | never> {
    const id = route.params['tagId'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tags: HttpResponse<ITags>) => {
          if (tags.body) {
            return of(tags.body);
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
