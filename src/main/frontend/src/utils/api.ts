import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from "axios";

import {
  momentRequestInterceptor,
  momentResponseInterceptor,
  securityErrorInterceptor,
  csrfRequestInterceptor,
  loadingRequestInterceptor,
  loadingResponseInterceptor,
  loadingErrorInterceptor,
} from './interceptor';

export class Api {

  private api: AxiosInstance;

  public constructor(config: AxiosRequestConfig = { withCredentials: true }) {
    this.api = axios.create(config);

    // Request interceptors
    this.api.interceptors.request.use(loadingRequestInterceptor);
    this.api.interceptors.request.use(csrfRequestInterceptor);
    this.api.interceptors.request.use(momentRequestInterceptor);

    // Response interceptors
    this.api.interceptors.response.use(loadingResponseInterceptor, loadingErrorInterceptor);
    this.api.interceptors.response.use(undefined, securityErrorInterceptor);
    this.api.interceptors.response.use(momentResponseInterceptor);
  }

  protected get<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.get<T>(url, config)
      .catch((error: AxiosError) => this.handleError(error));
  }

  protected delete(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse> {
    return this.api.delete(url, config)
      .catch((error: AxiosError) => this.handleError(error));
  }

  protected post<R, T>(url: string, data?: R | null, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.post<T>(url, data, config)
      .catch((error: AxiosError) => this.handleError(error));
  }

  protected put<R, T>(url: string, data?: R | null, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.api.put<T>(url, data, config)
      .catch((error: AxiosError) => this.handleError(error));
  }

  protected submit<T = any>(url: string, data?: FormData, config: AxiosRequestConfig = {}): Promise<AxiosResponse<T>> {
    const headers = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };

    config = {
      ...config,
      headers: config.headers ? { ...config.headers, ...headers } : { ...headers },
    };

    return this.api.post<T>(url, data, config)
      .catch((error: AxiosError) => this.handleError(error));
  }

  private handleError<T>(error: AxiosError): Promise<AxiosResponse<T>> {
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      console.log(error.response);
    } else if (error.request) {
      // The request was made but no response was received
      // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
      // http.ClientRequest in node.js
      console.log(error.request);
    } else {
      // Something happened in setting up the request that triggered an Error
      console.log('Error', error.message);
    }
    console.log(error.config);

    throw error;
  }
}