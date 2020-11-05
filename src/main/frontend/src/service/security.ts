import { AxiosRequestConfig } from 'axios';

import { Api } from 'utils/api';
import { ObjectResponse, AxiosObjectResponse } from 'model/response';
import { CsrfResult } from 'model/security';

export class SecurityApi extends Api {

  constructor(config: AxiosRequestConfig = {}) {
    super(config);
  }

  public async login(username: string, password: string): Promise<AxiosObjectResponse<CsrfResult>> {
    const form = new FormData();

    form.append('username', username);
    form.append('password', password);

    const url = `/login`;

    return this.submit<ObjectResponse<CsrfResult>>(url, form);
  }

  public async logout(): Promise<AxiosObjectResponse<CsrfResult>> {
    const url = `/logout`;

    const config = {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    };

    return this.submit<ObjectResponse<CsrfResult>>(url, undefined, config);
  }

}
