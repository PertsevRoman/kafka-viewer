import {Component, OnInit} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {invokeAvroUpload, invokeSchemasUpdate, setSchema} from '../../store/actions';
import {selectBundledSchemes, selectS3Schemes, selectSchema} from '../../store/selector';

@Component({
  selector: 'app-schemes',
  templateUrl: './schemes.component.html',
  styleUrls: ['./schemes.component.styl']
})
export class SchemesComponent implements OnInit {
  bundledSchemes$ = this.store$
    .pipe(
      select(selectBundledSchemes),
    );

  s3Schemes$ = this.store$
    .pipe(
      select(selectS3Schemes),
    );

  schema$ = this.store$
    .pipe(
      select(selectSchema),
    );

  beforeUpload = (file: File) => {
    this.store$.dispatch(invokeAvroUpload({
      file,
    }));

    return false;
  }

  constructor(
    private store$: Store<RootState>,
  ) { }

  invokeUpdate() {
    this.store$.dispatch(invokeSchemasUpdate({}));
  }

  setSchema(schema: string) {
    this.store$.dispatch(setSchema({
      schema,
    }));
  }

  ngOnInit() { }
}
