import { MdlButtonModule, MdlIconModule } from '@angular-mdl/core';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ClipboardModule } from 'ngx-clipboard';

import { CopyTextModalComponent } from './copy-text-modal.component';

@NgModule({
  imports: [
    CommonModule,
    MdlButtonModule.forRoot(),
    MdlIconModule.forRoot(),
    ClipboardModule,
  ],
  declarations: [
    CopyTextModalComponent
  ],
  entryComponents: [
    CopyTextModalComponent
  ]
})
export class CopyTextModalModule {
}
