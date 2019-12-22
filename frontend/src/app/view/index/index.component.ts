import {Component, Inject, LOCALE_ID, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {
  selectAutoUpdate,
  selectCurrentPartitionIndex,
  selectCurrentTopicId,
  selectDataPresenceState,
  selectDecode,
  selectDirection,
  selectPartitions,
  selectPartitionsLoad,
  selectRecord,
  selectTopics,
  selectTopicsLoad
} from '../../store/selector';
import {ScrollSourceEffect} from '../../store/scroll-source.effect';
import {
  refresh,
  setCurrentPartitionIndex,
  setCurrentTopic,
  setDecodeState,
  setRecord,
  toggleDirection
} from '../../store/actions';
import {filter} from 'rxjs/operators';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.styl']
})
export class IndexComponent implements OnInit {
  /**
   * Topics list
   */
  public topics$: Observable<Topic[]> = this.store$
    .pipe(
      select(selectTopics),
    );

  /**
   * Direction
   */
  public direction$: Observable<Direction> = this.store$
    .pipe(
      select(selectDirection),
    );

  /**
   * Current topics ID
   */
  public currentTopicId$: Observable<number> = this.store$
    .pipe(select(selectCurrentTopicId));

  /**
   * Current partitions
   */
  public currentPartitions$: Observable<Partition[]> = this.store$.pipe(
    select(selectPartitions),
  );

  public partitionsLoad$: Observable<boolean> = this.store$.pipe(
    select(selectPartitionsLoad),
  );

  public topicsLoad$: Observable<boolean> = this.store$.pipe(
    select(selectTopicsLoad),
  );

  /**
   * Current partition index
   */
  public currentPartitionIndex$: Observable<PartitionIndex> = this.store$.pipe(
    select(selectCurrentPartitionIndex),
  );

  /**
   *
   */
  public currentRecord$: Observable<any | null> = this.store$.pipe(
    select(selectRecord),
    filter(record => !!record),
  );

  /**
   * Decode state
   */
  public decodeState$: Observable<boolean> = this.store$.pipe(
    select(selectDecode),
  );

  /**
   * Data presence state
   */
  public dataPresenceState$: Observable<boolean> = this.store$.pipe(
    select(selectDataPresenceState),
  );

  /**
   * Auto update state
   */
  public autoUpdate$: Observable<boolean> = this.store$.pipe(
    select(selectAutoUpdate),
  );

  constructor(
    private http: HttpClient,
    private store$: Store<RootState>,
    public scrollSourceEffect: ScrollSourceEffect,
    @Inject(LOCALE_ID) public locale: string
  ) { }

  /**
   *
   * @param record record object
   */
  setRecord(record: KRecord) {
    if (record && record.record) {
      this.store$.dispatch(setRecord({
        record,
      }));
    }
  }

  /**
   *
   * @param state decoded checkbox state
   */
  decodedChange(state: boolean) {
    this.store$.dispatch(setDecodeState({
      state,
    }));
  }

  toggleDirection() {
    this.store$.dispatch(toggleDirection({}));
  }

  refreshData() {
    this.store$.dispatch(refresh({}));
  }

  format(): string {
    if (this.locale === 'ru-RU') {
      return 'hh:mm:ss, dd.M.yy';
    } else {
      return 'h:mm:ss a, M/dd/yy';
    }
  }

  setTopic(topicIndex: number) {
    this.store$.dispatch(setCurrentTopic({
      topicIndex,
    }));
  }

  setPartitionIndex(partitionIndex) {
    this.store$.dispatch(setCurrentPartitionIndex({
      partitionIndex,
    }));
  }

  ngOnInit() { }
}
