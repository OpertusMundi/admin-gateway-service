import { AxiosRequestConfig } from 'axios';

import { Api } from 'utils/api';
import { ObjectResponse, PageRequest, Sorting, AxiosObjectResponse, AxiosPageResponse, PageResult, AxiosSimpleResponse } from 'model/response';
import { Account, AccountQuery, AccountFormData, AccountCommand, SetPasswordCommand, ProfileCommand } from 'model/account';

export default class AccountApi extends Api {

  constructor(config: AxiosRequestConfig = {}) {
    super(config);
  }

  public createNew(): AccountCommand {
    return {
      active: true,
      blocked: false,
      email: '',
      firstName: '',
      image: null,
      imageMimeType: null,
      lastName: '',
      locale: 'en',
      mobile: '',
      phone: '',
      roles: [],
      username: '',
      password: '',
      passwordMatch: '',
    };
  }

  public async find(query: Partial<AccountQuery>, pageRequest: PageRequest, sorting: Sorting[]): Promise<AxiosPageResponse<Account>> {
    const { page, size } = pageRequest;
    const { id: field, order } = sorting[0];

    const queryString = (Object.keys(query) as Array<keyof AccountQuery>)
      .reduce((result: string[], key: keyof AccountQuery) => {
        return query[key] !== null ? [...result, `${key}=${query[key]}`] : result;
      }, []);

    const url = `/action/admin/accounts?page=${page}&size=${size}&${queryString.join('&')}&orderBy=${field}&order=${order}`;

    return this.get<ObjectResponse<PageResult<Account>>>(url);
  }

  public async findOne(id: number): Promise<AxiosObjectResponse<AccountFormData>> {
    const url = `/action/admin/accounts/${id}`;


    return this.get<ObjectResponse<AccountFormData>>(url);
  }

  public async create(command: AccountCommand): Promise<AxiosObjectResponse<Account>> {
    const url = `/action/admin/accounts/`;

    return this.post<AccountCommand, ObjectResponse<Account>>(url, command);
  }

  public async update(id: number, command: AccountCommand): Promise<AxiosObjectResponse<Account>> {
    const url = `/action/admin/accounts/${id}`;

    return this.post<AccountCommand, ObjectResponse<Account>>(url, command);
  }

  public async remove(id: number): Promise<AxiosSimpleResponse> {
    const url = `/action/admin/accounts/${id}`;

    return this.delete(url);
  }

  public async setPassword(command: SetPasswordCommand): Promise<AxiosObjectResponse<Account>> {
    const url = `/action/user/password`;

    return this.post<SetPasswordCommand, ObjectResponse<Account>>(url, command);
  }

  public async getProfile(): Promise<AxiosObjectResponse<Account>> {
    const url = `/action/user/profile`;

    return this.get<ObjectResponse<Account>>(url);
  }

  public async setProfile(command: ProfileCommand): Promise<AxiosObjectResponse<Account>> {
    const url = `/action/user/profile`;

    return this.post<ProfileCommand, ObjectResponse<Account>>(url, command);
  }

  public accountToCommand(account: Account): AccountCommand {
    const {
      createdOn,
      emailVerified,
      emailVerifiedOn,
      key,
      modifiedOn,
      ...rest
    } = account;

    const command: AccountCommand = {
      ...rest,
      password: '',
      passwordMatch: '',
    };

    return command;
  }
}
