export type Json =
  | string
  | number
  | boolean
  | null
  | JsonObject
  | JsonArray;

interface JsonArray extends Array<Json> { }

export interface JsonObject {
  [property: string]: Json;
}
