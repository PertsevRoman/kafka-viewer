import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SchemesService {
  constructor(private http: HttpClient) { }

  /**
   * Fetch schemes module presence
   */
  public fetchStatus(): Observable<boolean> {
    return this.http.get<boolean>(`/api/v1/schemes/status`);
  }

  /**
   * Invoke refresh schemes
   */
  public refresh() {
    return this.http.post(`/api/v1/schemes/refresh`, null);
  }

  /**
   * Get bundled schemes
   */
  public getBundledSchemes(): Observable<{
    bundledSchemas: BundledSchema[]
  }> {
    return this.http.get<{
      bundledSchemas: BundledSchema[],
    }>(`/api/v1/schemes/bundled`);
  }

  public uploadSchema(file: File) {
    const upload = new FormData();
    upload.append('file', file, file.name);

    return this.http.post(`/api/v1/schemes/upload`, upload);
  }

  // public getAvroSchemesVersion(): Observable<string> {
  // }

  /**
   * Fetch s3 AVRO schemes
   */
  public getS3Schemes(): Observable<{
    s3Schemas: S3Schema[]
  }> {
    return this.http.get<{
      s3Schemas: S3Schema[]
    }>(`/api/v1/schemes/s3`);
  }
}
