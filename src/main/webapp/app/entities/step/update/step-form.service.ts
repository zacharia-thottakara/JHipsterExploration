import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStep, NewStep } from '../step.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStep for edit and NewStepFormGroupInput for create.
 */
type StepFormGroupInput = IStep | PartialWithRequiredKeyOf<NewStep>;

type StepFormDefaults = Pick<NewStep, 'id'>;

type StepFormGroupContent = {
  id: FormControl<IStep['id'] | NewStep['id']>;
  name: FormControl<IStep['name']>;
  note: FormControl<IStep['note']>;
  do_next: FormControl<IStep['do_next']>;
  dependent_on: FormControl<IStep['dependent_on']>;
  action_of: FormControl<IStep['action_of']>;
  action_for: FormControl<IStep['action_for']>;
};

export type StepFormGroup = FormGroup<StepFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StepFormService {
  createStepFormGroup(step: StepFormGroupInput = { id: null }): StepFormGroup {
    const stepRawValue = {
      ...this.getFormDefaults(),
      ...step,
    };
    return new FormGroup<StepFormGroupContent>({
      id: new FormControl(
        { value: stepRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(stepRawValue.name, {
        validators: [Validators.required],
      }),
      note: new FormControl(stepRawValue.note),
      do_next: new FormControl(stepRawValue.do_next),
      dependent_on: new FormControl(stepRawValue.dependent_on),
      action_of: new FormControl(stepRawValue.action_of),
      action_for: new FormControl(stepRawValue.action_for),
    });
  }

  getStep(form: StepFormGroup): IStep | NewStep {
    return form.getRawValue() as IStep | NewStep;
  }

  resetForm(form: StepFormGroup, step: StepFormGroupInput): void {
    const stepRawValue = { ...this.getFormDefaults(), ...step };
    form.reset(
      {
        ...stepRawValue,
        id: { value: stepRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): StepFormDefaults {
    return {
      id: null,
    };
  }
}
