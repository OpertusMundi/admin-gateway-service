
import { ThunkAction } from 'redux-thunk'
import { JsonObject } from 'model/json';
import { RootState } from 'store';
import { MessageTypes } from 'store/i18n/types';
import { loadMessagesInit, loadMessagesComplete } from 'store/i18n/actions'
import { MessageApi } from 'service/i18n';

type ThunkResult<R> = ThunkAction<R, RootState, unknown, MessageTypes>;

export const fetchMessages = (locale: string): ThunkResult<Promise<JsonObject>> => async (dispatch) => {
  dispatch(loadMessagesInit(locale));

  const api = new MessageApi();

  const messages = await api.getMessages(locale);

  dispatch(loadMessagesComplete(locale, messages));

  return messages;
}

export const changeLocale = (locale: string): ThunkResult<void> => (dispatch) => {
  dispatch(fetchMessages(locale)).catch(
    () => console.warn("No messages for locale " + locale));
};
