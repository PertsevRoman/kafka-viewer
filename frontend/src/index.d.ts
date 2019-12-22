type PartitionIndex = number | 'all';

type Partition = {
  index: PartitionIndex;
  messageCount: number;
};

type Topic = {
  name: string;
  id: number;
};

type KRecord = {
  timestamp: number;
  type: string;
  record: any;
  index: number;
  partition: number;
};

type RootState = {
  topics: Topic[];
  topicsLoad: boolean;
  currentTopic: number;
  partitions: Partition[];
  partitionsLoad: boolean;
  currentPartition: PartitionIndex;
  viewRange: {
    start: number;
    end: number;
  };
  record: any | null;
  decode: boolean;
  s3repoAvailable: boolean;
  bundledSchemes: BundledSchema[];
  s3Schemas: S3Schema[];
  schema: object;
  clusters: ClusterInfo[];
  clustersLoad: boolean;
  currentCluster: number;
  autoUpdate: boolean;
  avroSchemesVesrion: string;
  direction: Direction;
  lastTimestamp: number;
};

type Schema = {
  schema: any;
};

type S3Schema = Schema & {
  key: string;
};

type BundledSchema = Schema & {
  clName: string; // class name
};

type ClusterInfo = {
  index: number;
  host: string;
};

type Direction = 'asc' | 'desc';
