import {createSelector} from '@ngrx/store';

/**
 * Select root feature
 * @param state
 */
export const selectRootFeature = (state): RootState => state.root;

/**
 * Select topics
 */
export const selectTopics = createSelector(
  selectRootFeature,
  (root: RootState): Topic[] => root.topics,
);

/**
 * Select current topic ID
 */
export const selectCurrentTopicId = createSelector(
  selectRootFeature,
  (root: RootState): number => root.currentTopic,
);

/**
 * Select current topic name
 */
export const selectCurrentTopicName = createSelector(
  selectRootFeature,
  (root: RootState): string | null => {
    const topic = root.topics.find(({ id }) => id === root.currentTopic);

    return topic ? topic.name : null;
  },
);

export const selectCurrentTopicInfo = createSelector(
  selectRootFeature,
  (root: RootState) => {
    const topic = root.topics.find(({ id }) => id === root.currentTopic);
    const partition = root.partitions.find(({ index }) => index === root.currentPartition);

    return {
      autoUpdate: root.autoUpdate,
      topicName: topic ? topic.name : null,
      clusterIndex: root.currentCluster,
      direction: root.direction,
      partition: root.currentPartition,
      messageCount: partition ? partition.messageCount : null,
    };
  },
);

export const selectCurrentCursor = createSelector(
  selectRootFeature,
  (root: RootState) => {
    const topic = root.topics.find(({ id }) => id === root.currentTopic);
    const partition = root.partitions.find(({ index }) => index === root.currentPartition);

    if (!topic) {
      return null;
    }

    return {
      topicName: topic.name,
      clusterIndex: root.currentCluster,
      partition: root.currentPartition,
      offset: partition.messageCount,
      timestamp: root.lastTimestamp,
    };
  },
);

/**
 * Select partitions
 */
export const selectPartitions = createSelector(
  selectRootFeature,
  (root: RootState) => root.partitions.sort((a, b) => {
    if (a.index === 'all' && b.index === 'all') {
      return 0;
    } else if (a.index === 'all') {
      return -1;
    } else if (b.index === 'all') {
      return 1;
    }

    return a.index - b.index;
  }),
);

/**
 *
 */
export const selectCurrentPartitionIndex = createSelector(
  selectRootFeature,
  (root: RootState) => root.currentPartition,
);

/**
 * Get current partition message count
 */
export const selectCurrentPartitionMessageCount = createSelector(
  selectRootFeature,
  (root: RootState) => {
    const partition = root.partitions.find(({ index }) => index === root.currentPartition);
    return partition ? partition.messageCount : 0;
  }
);

/**
 * Select root feature
 */
export const selectRecord = createSelector(
  selectRootFeature,
  (root: RootState) => root.record,
);

/**
 * Select decode state
 */
export const selectDecode = createSelector(
  selectRootFeature,
  (root: RootState) => root.decode,
);

/**
 * Select data presence state
 */
export const selectDataPresenceState = createSelector(
  selectRootFeature,
  (root: RootState) => !!root.topics.length,
);


export const selectS3RepoAvailability = createSelector(
  selectRootFeature,
  (root: RootState) => root.s3repoAvailable,
);

export const selectBundledSchemes = createSelector(
  selectRootFeature,
  (root: RootState) => root.bundledSchemes,
);

export const selectS3Schemes = createSelector(
  selectRootFeature,
  (root: RootState) => root.s3Schemas,
);

export const selectSchema = createSelector(
  selectRootFeature,
  (root: RootState) => root.schema,
);

export const selectClusters = createSelector(
  selectRootFeature,
  (root: RootState) => root.clusters,
);

export const selectCurrentCluster = createSelector(
  selectRootFeature,
  (root: RootState) => root.currentCluster,
);

export const selectAutoUpdate = createSelector(
  selectRootFeature,
  (root: RootState) => root.autoUpdate,
);

export const selectClustersLoad = createSelector(
  selectRootFeature,
  (root: RootState) => root.clustersLoad,
);

export const selectDirection = createSelector(
  selectRootFeature,
  (root: RootState) => root.direction,
);

export const selectTopicsLoad = createSelector(
  selectRootFeature,
  (root: RootState) => root.clustersLoad || root.topicsLoad,
);

export const selectPartitionsLoad = createSelector(
  selectRootFeature,
  (root: RootState) => root.clustersLoad || root.topicsLoad || root.partitionsLoad,
);
