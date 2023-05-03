import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IProfiles, NewProfiles } from '../profiles.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { profileId: unknown }> = Partial<Omit<T, 'profileId'>> & { profileId: T['profileId'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfiles for edit and NewProfilesFormGroupInput for create.
 */
type ProfilesFormGroupInput = IProfiles | PartialWithRequiredKeyOf<NewProfiles>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IProfiles | NewProfiles> = Omit<T, 'createdOn' | 'modifiedOn'> & {
  createdOn?: string | null;
  modifiedOn?: string | null;
};

type ProfilesFormRawValue = FormValueOf<IProfiles>;

type NewProfilesFormRawValue = FormValueOf<NewProfiles>;

type ProfilesFormDefaults = Pick<NewProfiles, 'profileId' | 'createdOn' | 'modifiedOn'>;

type ProfilesFormGroupContent = {
  profileId: FormControl<ProfilesFormRawValue['profileId'] | NewProfiles['profileId']>;
  username: FormControl<ProfilesFormRawValue['username']>;
  nickname: FormControl<ProfilesFormRawValue['nickname']>;
  birthday: FormControl<ProfilesFormRawValue['birthday']>;
  bio: FormControl<ProfilesFormRawValue['bio']>;
  createdBy: FormControl<ProfilesFormRawValue['createdBy']>;
  createdOn: FormControl<ProfilesFormRawValue['createdOn']>;
  modifiedBy: FormControl<ProfilesFormRawValue['modifiedBy']>;
  modifiedOn: FormControl<ProfilesFormRawValue['modifiedOn']>;
};

export type ProfilesFormGroup = FormGroup<ProfilesFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfilesFormService {
  createProfilesFormGroup(profiles: ProfilesFormGroupInput = { profileId: null }): ProfilesFormGroup {
    const profilesRawValue = this.convertProfilesToProfilesRawValue({
      ...this.getFormDefaults(),
      ...profiles,
    });
    return new FormGroup<ProfilesFormGroupContent>({
      profileId: new FormControl(
        { value: profilesRawValue.profileId, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      username: new FormControl(profilesRawValue.username),
      nickname: new FormControl(profilesRawValue.nickname),
      birthday: new FormControl(profilesRawValue.birthday),
      bio: new FormControl(profilesRawValue.bio),
      createdBy: new FormControl(profilesRawValue.createdBy),
      createdOn: new FormControl(profilesRawValue.createdOn),
      modifiedBy: new FormControl(profilesRawValue.modifiedBy),
      modifiedOn: new FormControl(profilesRawValue.modifiedOn),
    });
  }

  getProfiles(form: ProfilesFormGroup): IProfiles | NewProfiles {
    return this.convertProfilesRawValueToProfiles(form.getRawValue() as ProfilesFormRawValue | NewProfilesFormRawValue);
  }

  resetForm(form: ProfilesFormGroup, profiles: ProfilesFormGroupInput): void {
    const profilesRawValue = this.convertProfilesToProfilesRawValue({ ...this.getFormDefaults(), ...profiles });
    form.reset(
      {
        ...profilesRawValue,
        profileId: { value: profilesRawValue.profileId, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ProfilesFormDefaults {
    const currentTime = dayjs();

    return {
      profileId: null,
      createdOn: currentTime,
      modifiedOn: currentTime,
    };
  }

  private convertProfilesRawValueToProfiles(rawProfiles: ProfilesFormRawValue | NewProfilesFormRawValue): IProfiles | NewProfiles {
    return {
      ...rawProfiles,
      createdOn: dayjs(rawProfiles.createdOn, DATE_TIME_FORMAT),
      modifiedOn: dayjs(rawProfiles.modifiedOn, DATE_TIME_FORMAT),
    };
  }

  private convertProfilesToProfilesRawValue(
    profiles: IProfiles | (Partial<NewProfiles> & ProfilesFormDefaults)
  ): ProfilesFormRawValue | PartialWithRequiredKeyOf<NewProfilesFormRawValue> {
    return {
      ...profiles,
      createdOn: profiles.createdOn ? profiles.createdOn.format(DATE_TIME_FORMAT) : undefined,
      modifiedOn: profiles.modifiedOn ? profiles.modifiedOn.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
