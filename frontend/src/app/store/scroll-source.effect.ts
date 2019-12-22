import {CollectionViewer, DataSource} from '@angular/cdk/collections';
import {BehaviorSubject, Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {select, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {
  addMessages, setCurrentCluster,
  setCurrentPartitionIndex,
  setCurrentTopic,
  setDirection,
  setLastTimestamp,
  toggleDirection,
} from './actions';
import {filter, map, switchMap, take, tap, withLatestFrom} from 'rxjs/operators';
import {selectAutoUpdate, selectDirection} from './selector';

/**
 * Index to insert
 *
 * @param array Array
 * @param value Value to insert
 * @param comparator Compare function
 */
const sortedIndex = <T>(array: T[], value: T, comparator: (a: T, b: T) => boolean = (a, b) => a < b) => {
  let low = 0;
  let high = array.length;

  while (low < high) {
    const mid: number = low + high >>> 1;

    if (comparator(array[mid], value)) {
      low = mid + 1;
    } else {
      high = mid;
    }
  }

  return low;
};

@Injectable()
export class ScrollSourceEffect extends DataSource<KRecord> {

  /**
   * Cached data
   */
  private cachedData = Array.from<KRecord>({length: 0});

  /**
   * Data stream
   */
  public dataStream = new BehaviorSubject<KRecord[]>(this.cachedData);

  /**
   * Current modal load message ID
   */
  private messageId: string | null = null;

  onToggleDirection$ = createEffect(() => this.actions$
    .pipe(
      ofType(toggleDirection),
      switchMap(() => this.store$.pipe(
        select(selectAutoUpdate),
        take(1),
      )),
      filter((autoUpdate: boolean) => !autoUpdate),
      switchMap(() => this.store$.pipe(
        select(selectDirection),
        take(1),
      )),
      map((direction: Direction) => setDirection({
        direction: direction === 'asc' ? 'desc' : 'asc'
      }))
    ));

  onWsMessages$ = createEffect(() => this.actions$
    .pipe(
      ofType(addMessages),
      map(({messages}) => messages),
      filter(messages => !!messages),
      withLatestFrom(this.store$.pipe(
        select(selectDirection),
      )),
      tap(([ messages, direction ]) => {
        messages.forEach((message: KRecord) => {
          const index = sortedIndex(this.cachedData, message, ((a, b) => direction === 'desc' ?
            a.timestamp > b.timestamp : a.timestamp < b.timestamp));

          this.cachedData.splice(index, 0, message);
        });
      }),
      tap(([ _, direction ]) => {
        this.cachedData.forEach((message: KRecord, index) => {
          message.index = direction === 'desc' ? this.cachedData.length - index - 1 : index;
        });
      }),
      tap(() => this.dataStream.next(this.cachedData)),
  ), {
    dispatch: false,
  });

  /**
   * View range
   */
  onChangeSelection$ = createEffect(() => this.actions$.pipe(
    ofType(
      setCurrentCluster,
      setCurrentTopic,
      setCurrentPartitionIndex,
      setDirection,
    ),
    tap(() => this.store$.dispatch(setLastTimestamp({
      timestamp: 0,
    }))),
    tap(() => this.cachedData = Array.from<KRecord>({length: 0})),
    tap(() => this.dataStream.next(this.cachedData)),
  ), {
    dispatch: false
  });

  connect(collectionViewer: CollectionViewer): Observable<any[]> {
    return this.dataStream;
  }

  disconnect(collectionViewer: CollectionViewer): void { }

  constructor(
    private http: HttpClient,
    private store$: Store<RootState>,
    private actions$: Actions,
  ) {
    super();
  }

}
