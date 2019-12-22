import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree} from '@angular/router';
import {Observable} from 'rxjs';
import {SchemesService} from './schemes.service';
import {Injectable} from '@angular/core';

@Injectable()
export class SchemesCanActivate implements CanActivate {

  constructor(private schemesService: SchemesService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
    Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.schemesService.fetchStatus();
  }
}
