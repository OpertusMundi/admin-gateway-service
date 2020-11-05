import { AxiosRequestConfig } from 'axios';
import { Api } from 'utils/api';
import { ApplicationConfiguration } from 'model/configuration';
import { AxiosObjectResponse, ObjectResponse } from 'model/response';

export class ConfigurationApi extends Api {

  constructor(config: AxiosRequestConfig = {}) {
    super(config);
  }

  public async getConfiguration(): Promise<AxiosObjectResponse<ApplicationConfiguration>> {
    const url = `/action/configuration`;

    const config = {
      withCredentials: true,
    };

    return this.get<ObjectResponse<ApplicationConfiguration>>(url, config);
  }

}
