import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IndexComponent} from './view/index/index.component';
import {SchemesComponent} from './view/schemes/schemes.component';
import {SchemesCanActivate} from './service/schemes-can.activate';

const routes: Routes = [
  {path: '', pathMatch: 'full', component: IndexComponent},
  {
    path: 'schemes',
    pathMatch: 'full',
    component: SchemesComponent,
    canActivate: [
      SchemesCanActivate
    ],
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [
    RouterModule
  ],
  providers: [
    SchemesCanActivate,
  ]
})
export class AppRoutingModule { }
