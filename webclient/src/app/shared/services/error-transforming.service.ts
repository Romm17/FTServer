import { Injectable } from '@angular/core';
import { ServerError } from '../models/server-error.model';

@Injectable()
export class ErrorTransformingService {

  transformServerError(err: ServerError): Error {
    let message = err.exception;
    if (!message) {
      message = 'ServerError';
    }
    let lastIndex = message.lastIndexOf('.');
    lastIndex = lastIndex === -1 ? 0 : (lastIndex + 1);
    const exception = message.substring(lastIndex);
    return new Error(ServerExeptions[exception]);
  }
}

export const ServerExeptions = {
  'NotValidPasswordException': 'Please check your username or password',
  'ServerError': 'Sorry, but server has Errror'
};
