import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TopicService {

  constructor(private http: HttpClient) { }

  /**
   *
   * @param clusterIndex Cluster index
   */
  public getTopics(clusterIndex: number): Observable<{
    names: string[],
  }> {
    return this.http.get<{
      names: string[],
    }>(`/api/v1/cluster/${clusterIndex}/topics/info`);
  }

  /**
   *
   * @param clusterIndex Cluster index
   * @param topicName Topic name
   */
  getTopicInfo(clusterIndex: number, topicName: string): Observable<{
    partitions: Partition[]
  }> {
    return this.http.get<{
      partitions: Partition[]
    }>(`/api/v1/cluster/${clusterIndex}/topics/${topicName}/info`);
  }

  /**
   *
   * @param clusterIndex Cluster index
   * @param topicName Topic name
   * @param partition Partition index
   * @param start Start offset
   * @param count Messages count
   * @param direction Messages fetch direction
   */
  getMessages(clusterIndex: number, topicName: string, partition: PartitionIndex,
              start: number, count: number, direction: Direction = 'asc'): Observable<{
    recordList: KRecord[],
    partition: number,
    offset: number,
    count: number,
    lastTimestamp: number,
  }> {
    const params = new HttpParams()
      .set('direction', direction);

    return this.http.get<{
      recordList: KRecord[],
      partition: number,
      offset: number,
      count: number,
      lastTimestamp: number,
    }>(`/api/v1/cluster/${clusterIndex}/topics/${topicName}/messages/partition/${partition}/offset/${start}/count/${count}`, {
      params,
    });
  }
}
