import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProfilesComponent } from './list/profiles.component';
import { ProfilesDetailComponent } from './detail/profiles-detail.component';
import { ProfilesUpdateComponent } from './update/profiles-update.component';
import { ProfilesDeleteDialogComponent } from './delete/profiles-delete-dialog.component';
import { ProfilesRoutingModule } from './route/profiles-routing.module';

@NgModule({
  imports: [SharedModule, ProfilesRoutingModule],
  declarations: [ProfilesComponent, ProfilesDetailComponent, ProfilesUpdateComponent, ProfilesDeleteDialogComponent],
})
export class ProfilesModule {}
