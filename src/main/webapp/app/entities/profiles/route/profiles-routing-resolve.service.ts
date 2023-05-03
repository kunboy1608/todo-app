import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProfiles } from '../profiles.model';
import { ProfilesService } from '../service/profiles.service';

@Injectable({ providedIn: 'root' })
export class ProfilesRoutingResolveService implements Resolve<IProfiles | null> {
  constructor(protected service: ProfilesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProfiles | null | never> {
    const id = route.params['profileId'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((profiles: HttpResponse<IProfiles>) => {
          if (profiles.body) {
            return of(profiles.body);
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
