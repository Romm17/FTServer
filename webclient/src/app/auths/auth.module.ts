import { AuthDataService } from './auth-data.service';
import { AuthComponent } from './auth.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SharedModule } from './../shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { BusyModule } from '../shared/busy/busy.module';
import { FormControlErrorMessagesModule } from '@shared/form-control-error-message/form-control-error-messages.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        ReactiveFormsModule,
        BusyModule,
        FormControlErrorMessagesModule
    ],
    declarations: [
        AuthComponent,
    ],
    exports: [
        AuthComponent,
    ],
    providers: [
      AuthDataService
    ]
})
export class AuthModule {

}
