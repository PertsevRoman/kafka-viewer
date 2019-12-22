import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {IndexComponent} from './view/index/index.component';
import {en_US, NgZorroAntdModule, NZ_I18N} from 'ng-zorro-antd';
import {NzUploadModule} from 'ng-zorro-antd/upload';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {registerLocaleData} from '@angular/common';
import en from '@angular/common/locales/en';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {environment} from '../environments/environment';
import {StoreModule} from '@ngrx/store';
import {rootReducer} from './store/store';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {EffectsModule} from '@ngrx/effects';
import {StoreEffects} from './store/effects';
import {ScrollSourceEffect} from './store/scroll-source.effect';
import {DecodeBase64Pipe} from './pipe/decode-base64.pipe';
import {SchemesComponent} from './view/schemes/schemes.component';
import {IconsProviderModule} from './module/icons-provider.module';
import {EllipsisPipe} from './pipe/ellipsis.pipe';
import {UpdateEffectsService} from './store/update-effects.service';
import {UploadAvroService} from './store/upload-avro.service';

registerLocaleData(en);

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    DecodeBase64Pipe,
    SchemesComponent,
    EllipsisPipe,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    IconsProviderModule,
    NgZorroAntdModule,
    FormsModule,
    HttpClientModule,
    NzUploadModule,
    BrowserAnimationsModule,
    ScrollingModule,
    NgxJsonViewerModule,
    EffectsModule.forRoot([
      StoreEffects,
      ScrollSourceEffect,
      UpdateEffectsService,
      UploadAvroService,
    ]),
    StoreModule.forRoot({
      root: rootReducer,
    }),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
    }),
  ],
  providers: [{
    provide: NZ_I18N,
    useValue: en_US
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
