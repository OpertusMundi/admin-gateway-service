import _ from 'lodash';
import { AxiosRequestConfig } from 'axios';
import { flatten } from 'flat';

import { JsonObject } from 'model/json';
import { Api } from 'utils/api';

export class MessageApi extends Api {

  constructor(config: AxiosRequestConfig = {}) {
    super(config);
  }

  public async getMessages(locale: string): Promise<Record<string, string>> {
    const url = `/i18n/${locale}/messages.json`;

    const config = {
      withCredentials: true,
    };

    const response = await this.get<JsonObject>(url, config);

    if (_.isEmpty(response.data) || !_.isObject(response.data)) {
      // TODO: Add client error
      throw new Error('Expected a non-empty object with keyed messages!');
    }
    else {
      // Convert to a flat object of keyed messages
      return flatten(response.data);
    }
  }

}
