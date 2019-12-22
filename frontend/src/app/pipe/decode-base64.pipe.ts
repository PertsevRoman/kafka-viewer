import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'decodeBase64'
})
export class DecodeBase64Pipe implements PipeTransform {

  transform(value: any, ...args: any[]): any {
    if (!value) { return value; }

    const [ state ] = args;

    if (!state) {
      return value;
    }

    const cp = JSON.parse(JSON.stringify(value));

    const walk = (node) => {
      if (!node) { return; }

      const keys = Object.keys(node);

      for (const key of keys) {
        if (key === 'bytes') {
          try {
            node[key] = atob(node[key]);
          } catch (e) {
          }
        }

        if (typeof node[key] === 'object') {
          walk(node[key]);
        }
      }
    };

    walk(cp);

    return cp;
  }
}
