import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClusterService {

  public getClusters(): Observable<ClusterInfo[]> {
    return this.http.get<ClusterInfo[]>(`/api/v1/clusters/info`);
  }

  constructor(private http: HttpClient) { }
}
