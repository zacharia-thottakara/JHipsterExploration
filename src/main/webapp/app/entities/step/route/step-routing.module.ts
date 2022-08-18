import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StepComponent } from '../list/step.component';
import { StepDetailComponent } from '../detail/step-detail.component';
import { StepUpdateComponent } from '../update/step-update.component';
import { StepRoutingResolveService } from './step-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const stepRoute: Routes = [
  {
    path: '',
    component: StepComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StepDetailComponent,
    resolve: {
      step: StepRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StepUpdateComponent,
    resolve: {
      step: StepRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StepUpdateComponent,
    resolve: {
      step: StepRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stepRoute)],
  exports: [RouterModule],
})
export class StepRoutingModule {}
