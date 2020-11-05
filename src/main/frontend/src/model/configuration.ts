interface OsmConfiguration {
  url: string;
}

interface BingMapsConfiguration {
  applicationKey: string;
  imagerySet: string;
}

interface MapConfiguration {
  center: [number, number];
}

export interface ApplicationConfiguration {
  bingMaps?: BingMapsConfiguration;
  map?: MapConfiguration;
  osm?: OsmConfiguration;
}
