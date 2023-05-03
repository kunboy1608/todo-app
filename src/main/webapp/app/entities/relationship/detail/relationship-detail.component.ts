import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRelationship } from '../relationship.model';

@Component({
  selector: 'jhi-relationship-detail',
  templateUrl: './relationship-detail.component.html',
})
export class RelationshipDetailComponent implements OnInit {
  relationship: IRelationship | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ relationship }) => {
      this.relationship = relationship;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
