import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {catchError, delay, filter, map, switchMap, take, tap} from 'rxjs/operators';
import {TopicService} from '../service/topic.service';
import {
  invokeSchemasUpdate,
  refresh,
  setBundledSchemas,
  setClusters,
  setClustersLoad,
  setCurrentCluster,
  setCurrentPartitionIndex,
  setCurrentPartitions,
  setCurrentTopic,
  setPartitionsLoad,
  setS3Schemas,
  setS3SchemesAvailableState,
  setTopics,
  setTopicsLoad
} from './actions';
import {of} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {selectCurrentCluster, selectCurrentTopicInfo} from './selector';
import {SchemesService} from '../service/schemes.service';
import {ClusterService} from '../service/cluster.service';

@Injectable()
export class StoreEffects {

  fetchS3StateOnInit$ = createEffect(() => this.actions$.pipe(
    ofType('@ngrx/effects/init'),
    switchMap(() => this.schemesService.fetchStatus()),
    catchError(() => of(setS3SchemesAvailableState({
      state: false,
    }))),
    map((state: boolean) => setS3SchemesAvailableState({
      state,
    })),
  ));

  fetchClustersOnInit$ = createEffect(() => this.actions$.pipe(
    ofType('@ngrx/effects/init'),
    tap(() => this.store$.dispatch(setClustersLoad({
      state: true,
    }))),
    switchMap(() => this.clusterService.getClusters()),
    tap(() => this.store$.dispatch(setClustersLoad({
      state: false,
    }))),
    map((clusters: ClusterInfo[]) => setClusters({
      clusters,
    })),
  ));

  fetchOnCluster$ = createEffect(() => this.actions$.pipe(
    ofType(setClusters),
    map(({ clusters }) => clusters),
    filter((clusters) => !!clusters),
    map(([ cluster ]) => cluster),
    map((cluster) => cluster.index),
    map((clusterIndex: number) => setCurrentCluster({
      currentCluster: clusterIndex,
    }))
  ));

  fetchTopicsOnInit$ = createEffect(() => this.actions$.pipe(
    ofType(refresh, setCurrentCluster),
    switchMap(() => this.store$.pipe(
      select(selectCurrentCluster),
      take(1),
    )),
    tap(() => this.store$.dispatch(setTopicsLoad({
      state: true,
    }))),
    switchMap((clusterIndex: number) => this.topicsService.getTopics(clusterIndex)),
    tap(() => this.store$.dispatch(setTopicsLoad({
      state: false,
    }))),
    map(({ names }) => names),
    map((names) => names.map((name, index: number) => ({
      id: index,
      name,
    } as Topic))),
    map((topics: Topic[]) => setTopics({
      topics,
    })),
  ));

  fetchBundledSchemas$ = createEffect(() => this.actions$.pipe(
    ofType('@ngrx/effects/init'),
    switchMap(() => this.schemesService.getBundledSchemes()),
    map(({bundledSchemas}: {
      bundledSchemas: BundledSchema[]
    }) => bundledSchemas),
    map((schemas: BundledSchema[]) => setBundledSchemas({
      schemas,
    }))
  ));

  fetchS3Schemas$ = createEffect(() => this.actions$.pipe(
    ofType('@ngrx/effects/init'),
    switchMap(() => this.schemesService.getS3Schemes()),
    map(({s3Schemas}: {
      s3Schemas: S3Schema[]
    }) => s3Schemas),
    map((schemas: S3Schema[]) => setS3Schemas({
      schemas,
    }))
  ));

  invokeSchemasInit$ = createEffect(() => this.actions$.pipe(
    ofType(invokeSchemasUpdate),
    switchMap(() => this.schemesService.refresh()),
  ), {
    dispatch: false,
  });

  fetchBundledAfterUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(invokeSchemasUpdate),
    delay(3500),
    switchMap(() => this.schemesService.getBundledSchemes()),
    map(({bundledSchemas}: {
      bundledSchemas: BundledSchema[]
    }) => bundledSchemas),
    map((schemas: BundledSchema[]) => setBundledSchemas({
      schemas,
    }))
  ));

  fetchS3AfterUpdate$ = createEffect(() => this.actions$.pipe(
    ofType(invokeSchemasUpdate),
    delay(3500),
    switchMap(() => this.schemesService.getS3Schemes()),
    map(({s3Schemas}: {
      s3Schemas: S3Schema[]
    }) => s3Schemas),
    map((schemas: S3Schema[]) => setS3Schemas({
      schemas,
    }))
  ));

  onTopicsSet$ = createEffect(() => this.actions$.pipe(
    ofType(setTopics),
    map(({ topics }) => topics),
    filter((topics) => !!topics.length),
    map(([ topic ]) => topic),
    map(({ id }) => id),
    map((topicId: number) => setCurrentTopic({
      topicIndex: topicId,
    })),
  ));

  onTopicIndexSet$ = createEffect(() => this.actions$
    .pipe(
      ofType(setCurrentTopic),
      switchMap(() => this.store$
        .pipe(
          select(selectCurrentTopicInfo),
          take(1),
        ),
      ),
      filter(({ topicName }) => !!topicName),
      tap(() => this.store$.dispatch(setPartitionsLoad({
        state: true,
      }))),
      switchMap(({ topicName, clusterIndex }) => this.topicsService.getTopicInfo(clusterIndex, topicName)),
      tap(() => this.store$.dispatch(setPartitionsLoad({
        state: false,
      }))),
      switchMap((partitionsInfo: {
        partitions: Partition[]
      }) => of(setCurrentPartitions(partitionsInfo))),
    ));

  onPartitionsSet$ = createEffect(() => this.actions$
    .pipe(
      ofType(setCurrentPartitions),
      switchMap(() => of(setCurrentPartitionIndex({
        partitionIndex: 'all',
      }))),
    )
  );

  constructor(
    private actions$: Actions,
    private store$: Store<RootState>,
    private topicsService: TopicService,
    private schemesService: SchemesService,
    private clusterService: ClusterService,
  ) { }
}
