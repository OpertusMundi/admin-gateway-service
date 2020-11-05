import moment from 'utils/moment-localized';

import {
  MESSAGES_LOAD_COMPLETE,
  MessageTypes,
  MessageState,
} from "./types";

const initialState: MessageState = {
  locale: 'en',
  messages: {},
};

export function messageReducer(
  state = initialState,
  action: MessageTypes
): MessageState {

  switch (action.type) {
    case MESSAGES_LOAD_COMPLETE:
      // Update moment locale
      moment.locale(action.locale);

      return {
        ...state,
        locale: action.locale,
        messages: {
          ...state.messages,
          [action.locale]: action.messages,
        }
      };

    default:
      return state;
  }
}
