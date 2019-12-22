import {Action, createReducer, on} from '@ngrx/store';
import {
  addMessages,
  setAutoUpdate,
  setBundledSchemas,
  setClusters,
  setClustersLoad,
  setCurrentCluster,
  setCurrentPartitionIndex,
  setCurrentPartitions,
  setCurrentTopic,
  setDecodeState,
  setDirection,
  setLastTimestamp,
  setPartitionsLoad,
  setRecord,
  setS3Schemas,
  setS3SchemesAvailableState,
  setSchema,
  setTopics,
  setTopicsLoad,
  setViewRange
} from './actions';

export const initialState: RootState = {
  topics: [],
  topicsLoad: false,
  currentTopic: 0,
  partitions: [],
  partitionsLoad: false,
  currentPartition: 'all',
  viewRange: {
    start: 0,
    end: 0,
  },
  record: {},
  decode: true,
  s3repoAvailable: false,
  bundledSchemes: [],
  s3Schemas: [],
  schema: {},
  clusters: [],
  clustersLoad: false,
  currentCluster: 0,
  autoUpdate: false,
  avroSchemesVesrion: '',
  direction: 'desc',
  lastTimestamp: 0,
};

export function rootReducer(rstate: RootState, raction: Action) {
  return createReducer(initialState,
    on(setTopics, (state, action) => ({
      ...state,
      topics: action.topics,
    })),
    on(setCurrentTopic, (state, action) => ({
      ...state,
      currentTopic: action.topicIndex,
    })),
    on(addMessages, (state, action) => ({
      ...state,
      partitions:
        state.partitions.map(({ index, messageCount }) => ({
          index,
          messageCount: messageCount + action.messages
            .filter(({ partition }) => index === 'all' || index === partition)
            .map(() => 1)
            .reduce((count, initialCount = 0) => initialCount + count, 0),
        } as Partition)),
    })),
    on(addMessages, (state, action) => ({
      ...state,
      lastTimestamp: !!action.messages.length ?
        state.lastTimestamp < action.messages[action.messages.length - 1].timestamp + 1 ?
          action.messages[action.messages.length - 1].timestamp  + 1 : state.lastTimestamp
        : state.lastTimestamp,
    })),
    on(setCurrentPartitions, (state, action) => ({
      ...state,
      partitions: [
        {
          index: 'all',
          messageCount: action.partitions
            .map(({ messageCount }) => messageCount)
            .reduce((count, collect = 0) => count + collect, 0),
        } as Partition,
        ...action.partitions,
      ],
    })),
    on(setViewRange, (state, action) => ({
      ...state,
      viewRange: action.range,
    })),
    on(setRecord, (state, action) => ({
      ...state,
      record: action.record,
    })),
    on(setCurrentPartitionIndex, (state, action) => ({
      ...state,
      currentPartition: action.partitionIndex,
    })),
    on(setDecodeState, (state, action) => ({
      ...state,
      decode: action.state,
    })),
    on(setS3SchemesAvailableState, (state, action) => ({
      ...state,
      s3repoAvailable: action.state,
    })),
    on(setBundledSchemas, (state, action) => ({
      ...state,
      bundledSchemes: action.schemas,
    })),
    on(setS3Schemas, (state, action) => ({
      ...state,
      s3Schemas: action.schemas,
    })),
    on(setSchema, (state, action) => ({
      ...state,
      schema: JSON.parse(action.schema),
    })),
    on(setClusters, (state, action) => ({
      ...state,
      clusters: action.clusters,
    })),
    on(setCurrentCluster, (state, action) => ({
      ...state,
      currentCluster: action.currentCluster,
    })),
    on(setAutoUpdate, (state, action) => ({
      ...state,
      autoUpdate: action.autoUpdate,
    })),
    on(setClustersLoad, (state, action) => ({
      ...state,
      clustersLoad: action.state,
    })),
    on(setPartitionsLoad, (state, action) => ({
      ...state,
      partitionsLoad: action.state,
    })),
    on(setTopicsLoad, (state, action) => ({
      ...state,
      topicsLoad: action.state,
    })),
    on(setDirection, (state, action) => ({
      ...state,
      direction: action.direction,
    })),
    on(setLastTimestamp, (state, action) => ({
      ...state,
      lastTimestamp: action.timestamp,
    })),
  )(rstate, raction);
}
