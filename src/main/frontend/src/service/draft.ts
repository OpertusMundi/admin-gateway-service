import { AxiosRequestConfig } from 'axios';

import { Api } from 'utils/api';

import {
  AxiosObjectResponse,
  AxiosPageResponse,
  AxiosSimpleResponse,
  ObjectResponse,
  PageRequest,
  PageResult,
  SimpleResponse,
  Sorting,
} from 'model/response';

import { 
  AssetDraftQuery, 
  AssetDraft, 
  AssetDraftReviewCommand,
  EnumSortField,
} from 'model/draft';

export default class DraftApi extends Api {

  constructor(config: AxiosRequestConfig = {}) {
    super(config);
  }

  public async find(query: Partial<AssetDraftQuery>, pageRequest: PageRequest, sorting: Sorting<EnumSortField>[]): Promise<AxiosPageResponse<AssetDraft>> {
    const { page, size } = pageRequest;
    const { id: field, order } = sorting[0];

    const queryString = (Object.keys(query) as Array<keyof AssetDraftQuery>)
      .reduce((result: string[], key: keyof AssetDraftQuery) => {
        return query[key] !== null ? [...result, `${key}=${query[key]}`] : result;
      }, []);

    const url = `/action/provider/drafts?page=${page}&size=${size}&${queryString.join('&')}&orderBy=${field}&order=${order}`;

    return this.get<ObjectResponse<PageResult<AssetDraft>>>(url);
  }

  public async findOne(providerKey: string, draftKey: string): Promise<AxiosObjectResponse<AssetDraft>> {
    const url = `/action/provider/${providerKey}/drafts/${draftKey}`;


    return this.get<ObjectResponse<AssetDraft>>(url);
  }

  public async accept(providerKey: string, draftKey: string): Promise<AxiosSimpleResponse> {
    const url = `/action/provider/${providerKey}/drafts/${draftKey}`;

    const command: AssetDraftReviewCommand = {
      rejected: false,
    };


    return this.put<AssetDraftReviewCommand, SimpleResponse>(url, command);
  }

  public async reject(providerKey: string, draftKey: string, reason: string): Promise<AxiosSimpleResponse> {
    const url = `/action/provider/${providerKey}/drafts/${draftKey}`;

    const command: AssetDraftReviewCommand = {
      rejected: true,
      reason,
    };

    return this.put<AssetDraftReviewCommand, SimpleResponse>(url, command);
  }

}
