// Types
export interface MessageStore {
  [locale: string]: Record<string, string>;
}

// State
export interface MessageState {
  locale: string;
  messages: MessageStore,
}

// Actions
export const MESSAGES_LOAD_INIT = 'MESSAGES_LOAD_INIT'
export const MESSAGES_LOAD_COMPLETE = 'MESSAGES_LOAD_COMPLETE'

interface LoadMessagesInitAction {
  type: typeof MESSAGES_LOAD_INIT
  locale: string,
}

interface LoadMessagesCompleteAction {
  type: typeof MESSAGES_LOAD_COMPLETE,
  locale: string,
  messages: Record<string, string>,
}

export type MessageTypes = LoadMessagesInitAction | LoadMessagesCompleteAction
