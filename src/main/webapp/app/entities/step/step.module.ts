import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StepComponent } from './list/step.component';
import { StepDetailComponent } from './detail/step-detail.component';
import { StepUpdateComponent } from './update/step-update.component';
import { StepDeleteDialogComponent } from './delete/step-delete-dialog.component';
import { StepRoutingModule } from './route/step-routing.module';

@NgModule({
  imports: [SharedModule, StepRoutingModule],
  declarations: [StepComponent, StepDetailComponent, StepUpdateComponent, StepDeleteDialogComponent],
})
export class StepModule {}
