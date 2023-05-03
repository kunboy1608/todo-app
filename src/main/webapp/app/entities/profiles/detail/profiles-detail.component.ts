import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProfiles } from '../profiles.model';

@Component({
  selector: 'jhi-profiles-detail',
  templateUrl: './profiles-detail.component.html',
})
export class ProfilesDetailComponent implements OnInit {
  profiles: IProfiles | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profiles }) => {
      this.profiles = profiles;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
