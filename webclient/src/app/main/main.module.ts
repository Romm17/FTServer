import { CreateGroupComponent } from './create-group/create-group.component';
import { PaymentsFiltersComponent } from './payments-filters/payments-filters.component';
import { MdlSelectModule } from '@angular-mdl/select';
import { CreatePaymentComponent } from './create-payment/create-payment.component';
import { PaymentsListComponent } from './payments-list/payments-list.component';
import { SharedModule } from './../shared/shared.module';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { GroupListComponent } from './group-list/group-list.component';
import { NgModule } from '@angular/core';
import { AvatarModule } from 'ngx-avatar';
import { MdlModule, MdlDialogModule, MdlButtonModule } from '@angular-mdl/core';
import { BusyModule } from 'angular2-busy';
import { SearchComponent } from './search/search/search.component';
import { ClipboardModule } from 'ngx-clipboard';
import { MainPageComponent } from './main-page/main-page.component';
import { MdlDatePickerModule } from '@angular-mdl/datepicker';
import { AgmCoreModule } from '@agm/core';
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        MdlModule,
        MdlSelectModule,
        SharedModule,
        MdlDialogModule.forRoot(),
        BusyModule,
        AvatarModule,
        ClipboardModule,
        MdlDatePickerModule,
        AgmCoreModule.forRoot()

    ],
    declarations: [
        GroupListComponent,
        PaymentsListComponent,
        CreatePaymentComponent,
        MainPageComponent,
        PaymentsFiltersComponent,
        SearchComponent,
        CreateGroupComponent
    ],
    exports: [
    ],
    providers: [DatePipe]
})
export class MainModule {

}
