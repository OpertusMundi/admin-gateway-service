import HttpStatus from 'http-status-codes';

import { AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { showLoading, hideLoading } from 'react-redux-loading-bar';

import moment from './moment-localized';

import store from 'store';

import { EnumMessageLevel } from 'model/message';
import { SimpleResponse } from 'model/response';

const iso8601 = /^\d{4}-\d\d-\d\dT\d\d:\d\d:\d\d(\.\d+)?(([+-]\d\d:\d\d)|Z)?$/;

function isIso8601(value: any): boolean {
  // Check null
  if (!value) {
    return false;
  }
  // Check only string values
  if (typeof value !== 'string') {
    return false;
  }
  // Check regular expression
  return iso8601.test(value);
}

function convertStringToMoment(obj: any): void {
  // Check null
  if (!obj) {
    return obj;
  }
  // Check only objects
  if (typeof obj !== 'object') {
    return obj;
  }
  // Check every object property
  for (const key of Object.keys(obj)) {
    const value = obj[key];

    if (Array.isArray(value)) {
      // Handle arrays
      for (const item of value) {
        convertStringToMoment(item);
      }
    } else if (typeof value === 'object') {
      // Handle objects recursively
      convertStringToMoment(value);
    } else if (isIso8601(value)) {
      // Replace valid ISO date with moment instance
      obj[key] = moment(value);
    }
  }
}

export function csrfRequestInterceptor(value: AxiosRequestConfig): AxiosRequestConfig | Promise<AxiosRequestConfig> {
  const csrfToken = store ? store.getState().security.csrfToken : null

  const required = ['POST', 'PUT', 'DELETE'].includes((value.method as string).toUpperCase());

  const csrfHeader = { 'X-CSRF-TOKEN': csrfToken };

  const result = csrfToken && required ?
    {
      ...value,
      headers: value.headers ? { ...value.headers, ...csrfHeader } : csrfHeader
    }
    : {
      ...value
    };

  return result;
}

export function momentRequestInterceptor(value: AxiosRequestConfig): AxiosRequestConfig | Promise<AxiosRequestConfig> {
  return {
    ...value
  };
}

export function momentResponseInterceptor(value: AxiosResponse<any>): AxiosResponse<any> | Promise<AxiosResponse<any>> {
  convertStringToMoment(value.data);

  return value;
}

export function identityResponseInterceptor(value: AxiosResponse<any>): AxiosResponse<any> | Promise<AxiosResponse<any>> {
  return value;
}

/**
 * Executed whenever a error occurs. If a 401 status code is returned, the
 * user agent is redirected to the login page. API methods will return 401
 * when the user session is expired.
 * 
 * On all cases, the error response data is converted to a SimpleResponse
 * object.
 * 
 * @param error Axios error object
 */
export function securityErrorInterceptor(error: AxiosError<any>): Promise<AxiosError<SimpleResponse>> {
  const code = error.response?.status === HttpStatus.UNAUTHORIZED ? 'BasicMessageCode.Unauthorized' : 'BasicMessageCode.InternalServerError';
  const description = error.response?.status === HttpStatus.UNAUTHORIZED ? 'Access Denied' : 'Unknown Error';

  if (error.response?.status === HttpStatus.UNAUTHORIZED) {
    // If the API returns a 401 response, the user session must have been expired.

    // Since the session is expired, we cannot call logout to refresh the CSRF token; Hence
    // we are refreshing the page. We also check for the route URL to prevent infinite redirection
    // loop due to this interceptor. The /api/configuration call that returns 401 for unauthorized users.

    if (window.location.pathname !== '/workbench/login') {
      window.location.href = '/workbench/login';
    }
  }

  // Always sanitize errors to SimpleResponse objects
  const data: SimpleResponse = {
    success: false,
    messages: [{
      code,
      level: EnumMessageLevel.ERROR,
      description,
    }]
  };

  return Promise.reject({
    ...error,
    response: {
      ...error.response,
      data,
    }
  } as AxiosError<SimpleResponse>);
}

export function loadingRequestInterceptor(value: AxiosRequestConfig): AxiosRequestConfig | Promise<AxiosRequestConfig> {
  store.dispatch(showLoading());

  return {
    ...value
  };
}

export function loadingResponseInterceptor(value: AxiosResponse<any>): AxiosResponse<any> | Promise<AxiosResponse<any>> {
  store.dispatch(hideLoading());

  return value;
}

export function loadingErrorInterceptor(error: AxiosError<any>): Promise<AxiosError<any>> {
  store.dispatch(hideLoading());

  return Promise.reject(error);
}
