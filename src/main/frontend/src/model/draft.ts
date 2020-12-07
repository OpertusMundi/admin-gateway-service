import { Moment } from 'moment';

import { BasePricingModelCommand } from 'model/pricing-model';

export enum EnumDraftStatus {
  DRAFT = 'DRAFT',
  SUBMITTED = 'SUBMITTED',
  PENDING_HELPDESK_REVIEW = 'PENDING_HELPDESK_REVIEW',
  HELPDESK_REJECTED = 'HELPDESK_REJECTED',
  PENDING_PROVIDER_REVIEW = 'PENDING_PROVIDER_REVIEW',
  PROVIDER_REJECTED = 'PROVIDER_REJECTED',
  POST_PROCESSING = 'POST_PROCESSING',
  PUBLISHED = 'PUBLISHED',
}

interface BaseCatalogueItem {
  /*
   * An abstract of the resource
   */
  abstractText: string;
  /*
   * Auxiliary files or additional resources to the dataset
   */
  additionalResources: string;
  /*
   * Degree of conformity with the implementing rules/standard of the metadata followed
   */
  conformity: string;
  /*
   * Provides information about the datasets that the service operates on
   */
  coupledResource: string;
  /**
   * A point or period of time associated with the creation event in the lifecycle of the resource
   */
  creationDate: string;
  /*
   * The temporal extent of the resource (end date)
   */
  dateEnd: string;
  /*
   * The temporal extent of the resource (start date)
   */
  dateStart: string;
  /*
   * The file format, physical medium, or dimensions of the resource
   */
  format: string;
  /*
   * Geometry as GeoJSON
   */
  geometry: GeoJSON.Polygon;
  /*
   * The topic of the resource
   */
  keywords: string[];
  /*
   * A language of the resource
   */
  language: string;
  /*
   * Information about resource licensing
   */
  license: string;
  /*
   * General explanation of the data producer’s knowledge about the lineage of a dataset
   */
  lineage: string;
  /*
   * The date which specifies when the metadata record was created or updated
   */
  metadataDate: string;
  /*
   * The language in which the metadata elements are expressed
   */
  metadataLanguage: string;
  /*
   * The email of the organization responsible for the creation and maintenance of the metadata
   */
  metadataPointOfContactEmail: string;
  /*
   * The name of the organization responsible for the creation and maintenance of the metadata
   */
  metadataPointOfContactName: string;
  /*
   * Provides the ID of a parent dataset
   */
  parentId: string;
  /*
   * Information on the limitations and the reasons for them
   */
  publicAccessLimitations: string;
  /*
   * A point or period of time associated with the publication even in the
   * lifecycle of the resource
   */
  publicationDate: string;
  /*
   * Email of an entity responsible for making the resource available
   */
  publisherEmail: string;
  /*
   * Name of an entity responsible for making the resource available
   */
  publisherName: string;
  /*
   * Information about the reference system
   */
  referenceSystem: string;
  /*
   * The ‘navigation section’ of a metadata record which point users to the location (URL)
   * where the data can be downloaded, or to where additional information about the resource
   * may be provided
   */
  resourceLocator: string;
  /*
   * A point or period of time associated with the revision event in the lifecycle of the resource",
   */
  revisionDate: string;
  /*
   * Denominator of the scale of the data set
   */
  scale: string;
  /*
   * The nature or genre of the service
   */
  spatialDataServiceType: string;
  /*
   * Spatial resolution refers to the level of detail of the data set
   */
  spatialResolution: string;
  /*
   * A name given to the resource
   */
  title: string;
  /*
   * A high-level classification scheme to assist in the grouping and topic-based
   * search of available spatial data resources
   */
  topicCategory: string;
  /*
   * The nature or genre of the resource
   */
  type: string;
  /*
   * Version of the resource
   */
  version: string;
}

export interface CatalogueItemCommand extends BaseCatalogueItem {
  ingested: boolean;
  pricingModels: BasePricingModelCommand[];
  source: string;
}

export interface Publisher {
  /*
   * Publisher unique id
   */
  id: string;
  /*
   * Company location (city)
   */
  city: string;
  /*
   * Company country
   */
  country: string;
  /*
   * Provider registration date in ISO format e.g. 2020-06-10T16:01:04.991+03:00
   */
  joinedAt: string;
  /*
   * Base64 encoded company logo image
   */
  logoImage: string;
  /*
   * Company logo image mime type (used with image property to create a data URL)
   */
  logoImageMimeType: string;
  /*
   * Company name
   */
  name: string;
  /*
   * Average rating. If no user ratings exist, null is returned
   */
  rating: number | null;
}

export enum EnumSortField {
  CREATED_ON = 'CREATED_ON',
  MODIFIED_ON = 'MODIFIED_ON',
  PROVIDER = 'PROVIDER',
  STATUS = 'STATUS',
  TITLE = 'TITLE',
  VERSION = 'VERSION',
}

export interface AssetDraft {
  assetDraft: string;
  assetPublished: string;
  command: CatalogueItemCommand;
  createdOn: Moment;
  status: EnumDraftStatus;
  helpdeskRejectionReason: string;
  ingested: boolean;
  key: string;
  modifiedOn: Moment;
  providerRejectionReason: string;
  publisher: Publisher,
  source: string;
  title: string;
  version: string;
}

export interface AssetDraftQuery {
  status: EnumDraftStatus[];
  provider: string | null;
}

export interface AssetDraftReviewCommand {
  rejected: boolean;
  reason?: string;
}