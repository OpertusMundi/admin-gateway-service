export enum EnumMessageLevel {
  INFO = 'INFO',
  WARN = 'WARN',
  ERROR = 'ERROR',
}

export interface Message {
  arguments?: any;
  code: string;
  description: string;
  field?: string;
  level: EnumMessageLevel;
  value?: string;
}

