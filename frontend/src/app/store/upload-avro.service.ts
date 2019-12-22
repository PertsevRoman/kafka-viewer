import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Store} from '@ngrx/store';
import {invokeAvroUpload} from './actions';
import {map, switchMap} from 'rxjs/operators';
import {SchemesService} from '../service/schemes.service';


@Injectable()
export class UploadAvroService {

  onFileUpload$ = createEffect(() => this.actions$.pipe(
    ofType(invokeAvroUpload),
    map(({ file }) => file),
    switchMap((file: File) => this.schemesService.uploadSchema(file)),
  ), {
    dispatch: false,
  });

  constructor(private actions$: Actions,
              private store$: Store<RootState>,
              private schemesService: SchemesService) { }
}
