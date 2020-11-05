import {
  MESSAGES_LOAD_INIT,
  MESSAGES_LOAD_COMPLETE,
  MessageTypes,
} from './types';

// Action Creators
export function loadMessagesInit(locale: string): MessageTypes {
  return {
    type: MESSAGES_LOAD_INIT,
    locale,
  };
}

export function loadMessagesComplete(locale: string, messages: Record<string, string>): MessageTypes {
  return {
    type: MESSAGES_LOAD_COMPLETE,
    locale,
    messages,
  };
}
