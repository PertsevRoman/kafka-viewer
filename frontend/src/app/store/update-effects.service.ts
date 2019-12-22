import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {select, Store} from '@ngrx/store';
import {addMessages, setAutoUpdate, toggleAutoUpdateState} from './actions';
import {filter, map, share, switchMap, take, takeUntil, tap} from 'rxjs/operators';
import {selectAutoUpdate, selectCurrentCursor} from './selector';
import {fromEvent, zip} from 'rxjs';

/**
 * Make messages websocket URI
 */
const getWsUrl = (
  clusterIndex: number,
  topicName: string,
  offset: number,
  timestamp: number,
  partition: PartitionIndex,
) => `${location.protocol === 'https:' ? 'wss:' : 'ws:'}//${location.host}/messages?clusterIndex=${
  clusterIndex}&topicName=${topicName}&offset=${offset}&partition=${partition}&timestamp=${timestamp}`;

@Injectable()
export class UpdateEffectsService {

  openUpdate$ = this.actions$.pipe(
    ofType(toggleAutoUpdateState),
    switchMap(() => this.store$
      .pipe(
        select(selectAutoUpdate),
        take(1),
      )),
    filter((value) => !value),
    share(),
  );

  closeUpdate$ = this.actions$.pipe(
    ofType(toggleAutoUpdateState),
    switchMap(() => this.store$
      .pipe(
        select(selectAutoUpdate),
        take(1),
      )),
    filter((value) => !!value),
    share(),
  );

  makeWs$ = this.openUpdate$.pipe(
    switchMap(() => this.store$.pipe(
      select(selectCurrentCursor),
      take(1),
    )),
    map(({ clusterIndex, topicName, offset, partition, timestamp }) =>
      getWsUrl(clusterIndex, topicName, offset, timestamp, partition)),
    map((url: string) => new WebSocket(url)),
    share(),
  );

  openWsEvent$ = this.makeWs$.pipe(
    switchMap((ws: WebSocket) => fromEvent(
      ws, 'open',
    ).pipe(
      takeUntil(this.closeUpdate$),
    )),
  );

  closeWsEvent$ = this.makeWs$.pipe(
    switchMap((ws: WebSocket) => fromEvent(
      ws, 'close',
    ).pipe(
      takeUntil(this.closeUpdate$),
    )),
  );

  errorWsEvent$ = this.makeWs$.pipe(
    switchMap((ws: WebSocket) => fromEvent(
      ws, 'error',
    ).pipe(
      takeUntil(this.closeUpdate$),
    )),
  );

  messageWsEvent$ = this.makeWs$.pipe(
    switchMap((ws: WebSocket) => fromEvent(
      ws, 'message',
    ).pipe(
      takeUntil(this.closeUpdate$),
    )),
  );

  autoUpdateOnSocketOpen$ = createEffect(() => this.openWsEvent$
    .pipe(
      map(() => setAutoUpdate({
        autoUpdate: true,
      })),
    )
  );

  closeOnSocket$ = createEffect(() => zip(
    this.makeWs$,
    this.closeUpdate$,
  ).pipe(
    map(([ ws ]) => ws),
    tap((ws: WebSocket) => ws.close()),
    map(() => setAutoUpdate({
      autoUpdate: false,
    })),
  ));

  onWsClose$ = createEffect(() => this.closeWsEvent$
    .pipe(
      map(() => setAutoUpdate({
        autoUpdate: false,
      })),
    )
  );

  onMessage$ = createEffect(() => this.messageWsEvent$
    .pipe(
      map(({ data }: MessageEvent) => data),
      map((payload: string) => JSON.parse(payload)),
      map((messages: KRecord[]) => addMessages({
        messages,
      })),
    ));

  constructor(private actions$: Actions,
              private store$: Store<RootState>) { }
}
