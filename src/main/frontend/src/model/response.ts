import { AxiosResponse } from 'axios';

import { Message } from "model/message";

export interface SimpleResponse {
  success: boolean;
  messages: Message[];
}

export type AxiosSimpleResponse = AxiosResponse<SimpleResponse>;

export interface ObjectResponse<R> extends SimpleResponse {
  result: R;
}

export type AxiosObjectResponse<R> = AxiosResponse<ObjectResponse<R>>;

export interface PageRequest {
  page: number;
  size: number;
}

export type Order = 'asc' | 'desc';

export interface Sorting {
  id: string;
  order: Order;
}

export interface PageResult<R> {
  pageRequest: PageRequest;
  count: number;
  items: R[];
}

export type AxiosPageResponse<R> = AxiosResponse<ObjectResponse<PageResult<R>>>;
