import {Component} from '@angular/core';
import {select, Store} from '@ngrx/store';
import {Observable} from 'rxjs';
import {
  selectAutoUpdate,
  selectClusters,
  selectClustersLoad,
  selectCurrentCluster,
  selectS3RepoAvailability,
} from './store/selector';
import {setCurrentCluster, toggleAutoUpdateState,} from './store/actions';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent {
  isCollapsed = false;

  /**
   * Clusters list
   */
  public currentClusters$: Observable<ClusterInfo[]> = this.store$.pipe(
    select(selectClusters),
  );

  /**
   * Auto update state
   */
  public autoUpdate$: Observable<boolean> = this.store$.pipe(
    select(selectAutoUpdate),
  );

  public clustersLoad$: Observable<boolean> = this.store$.pipe(
    select(selectClustersLoad),
  );

  /**
   * Kafka cluster index
   */
  public currentClusterIndex$: Observable<number> = this.store$.pipe(
    select(selectCurrentCluster),
  );

  /**
   * S3 repo available
   */
  public selectS3RepoAvailable$: Observable<boolean> = this.store$.pipe(
    select(selectS3RepoAvailability),
  );

  /**
   * Set cluster state
   * @param clusterIndex Current index
   */
  public clusterChange(clusterIndex: number) {
    this.store$.dispatch(setCurrentCluster({
      currentCluster: clusterIndex,
    }));
  }

  /**
   * Toggle auto update
   */
  public toggleAutoUpdate() {
    this.store$.dispatch(toggleAutoUpdateState({}));
  }

  constructor(
    private store$: Store<RootState>,
  ) { }
}
