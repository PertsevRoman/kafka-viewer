import {createAction, props} from '@ngrx/store';
import {ListRange} from '@angular/cdk/collections';

export const setTopics = createAction('[Root] Set Topics', props<{
  topics: Topic[];
}>());

export const setCurrentTopic = createAction('[Root] Set Current Topic', props<{
  topicIndex: number;
}>());

export const setCurrentPartitions = createAction('[Root] Set Current Partitions', props<{
  partitions: Partition[];
}>());

export const setCurrentPartitionIndex = createAction('[Root] Set Current Partition Index', props<{
  partitionIndex: PartitionIndex;
}>());

export const setViewRange = createAction('[Root] Set View Range', props<{
  range: ListRange;
}>());

export const setRecord = createAction('[Root] Set Record', props<{
  record: any | null;
}>());

export const setDecodeState = createAction('[Root] Set Decode state', props<{
  state: boolean;
}>());

export const setS3SchemesAvailableState = createAction('[Root] Set S3 Schemes Available State', props<{
  state: boolean;
}>());

export const invokeSchemasUpdate = createAction('[Root] Invoke Schemas Update', props<{}>());

export const invokeAvroUpload = createAction('[Root] Invoke AVRO Upload', props<{
  file: File
}>());

export const setBundledSchemas = createAction('[Root] Set Bundled Schemas', props<{
  schemas: BundledSchema[],
}>());

export const setS3Schemas = createAction('[Root] Set S3 Schemas', props<{
  schemas: S3Schema[],
}>());

export const setSchema = createAction('[Root] Set Schema', props<{
  schema: string,
}>());

export const refresh = createAction('[Root] Refresh', props<{}>());

export const setClusters = createAction('[Root] Set Clusters', props<{
  clusters: ClusterInfo[],
}>());

export const setCurrentCluster = createAction('[Root] Set Current Cluster', props<{
  currentCluster: number,
}>());

export const setAutoUpdate = createAction('[Root] Set Auto Update', props<{
  autoUpdate: boolean,
}>());

export const toggleAutoUpdateState = createAction('[Root] Toggle Auto Update', props<{}>());

export const toggleDirection = createAction('[Root] Toggle Direction', props<{}>());

export const setDirection = createAction('[Root] Set Direction', props<{
  direction: Direction,
}>());

export const setClustersLoad = createAction('[Root] Set Clusters Load', props<{
  state: boolean,
}>());

export const setTopicsLoad = createAction('[Root] Set Topics Load', props<{
  state: boolean,
}>());

export const setPartitionsLoad = createAction('[Root] Set Partitions Load', props<{
  state: boolean,
}>());

export const setLastTimestamp = createAction('[Root] Set Last Timestamp', props<{
  timestamp: number,
}>());

export const addMessages = createAction('[Root] Add Messages', props<{
  messages: KRecord[],
}>());
