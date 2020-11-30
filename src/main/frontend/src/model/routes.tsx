import React from 'react';

/**
 * Libraries
 */
import * as pathToRegexp from 'path-to-regexp';

/**
 * Components
 */

/**
 * Icons
 */

import Icon from '@mdi/react';

import {
  mdiAccountMultiple,
  mdiBadgeAccountOutline,
  mdiCogOutline,
  mdiFaceAgent,
  mdiForumOutline,
  mdiHandshakeOutline,
  mdiMapOutline,
  mdiPackageVariantClosed,
  mdiSignatureFreehand,
  mdiTextBoxCheckOutline,
  mdiViewDashboardOutline,
} from '@mdi/js';

/**
 * Model
 */
import { RootState } from 'store';

import { EnumRole } from './role';
import { iconFunc } from './types';


/**
 * Routes for utility pages
 */
const Login = '/login';
const ResetPassword = '/reset-password';

export const Pages = {
  Login,
  ResetPassword,
};

/**
 * Static routes
 */

const Dashboard = '/dashboard';
const ConsumerManager = '/consumers';
const ContractMaster = '/contract/master';
const Map = '/map';
const MessageManager = '/messages';
const OrderManager = '/orders';
const Profile = '/profile';
const ProviderManager = '/providers'
const Settings = '/settings';
const AccountManager = '/users';
const DraftManager = '/drafts';

export const StaticRoutes = {
  Dashboard,
  ConsumerManager,
  ContractMaster,
  Map,
  MessageManager,
  OrderManager,
  Profile,
  ProviderManager,
  Settings,
  AccountManager,
  DraftManager,
};

/**
 * Dynamic routes
 */

const AccountCreate = '/admin/users/record/create';
const AccountUpdate = '/admin/users/record/update/:id';

export const DynamicRoutes = {
  AccountCreate,
  AccountUpdate,
};

/**
 * Routes for error pages
 */

const Unauthorized = '/error/401';
const Forbidden = '/error/403';
const NotFound = '/error/404';
const InternalServerError = '/error/500';

export const ErrorPages = {
  Unauthorized,
  Forbidden,
  NotFound,
  InternalServerError,
};

/**
 * Default links
 */
const defaultLinks = [Dashboard];

interface Route {
  breadcrumb?: boolean;
  description: string;
  icon?: iconFunc;
  title?: string;
  defaultTitle?: string;
  links?: string[];
  roles?: ((roles: EnumRole[], state: RootState) => boolean) | EnumRole[];
  toolbarComponent?: () => React.ReactChild;
  params?: string[];
}

interface RouteRegistry {
  [route: string]: Route;
}

const routes: RouteRegistry = {
  // Pages
  [Login]: {
    description: 'Login to workbench application',
  },
  [ResetPassword]: {
    description: 'Reset user password',
  },
  // Static
  [Dashboard]: {
    icon: (className?: string) => (<Icon path={mdiViewDashboardOutline} size="1.5rem" className={className} />),
    description: 'Initial page',
    title: 'links.dashboard',
    defaultTitle: 'Dashboard',
    links: []
  },
  [ConsumerManager]: {
    icon: (className?: string) => (<Icon path={mdiFaceAgent} size="1.5rem" className={className} />),
    description: 'Consumers',
    title: 'links.consumer-manager',
    defaultTitle: 'Consumers',
    links: defaultLinks
  },
  [ContractMaster]: {
    icon: (className?: string) => (<Icon path={mdiSignatureFreehand} size="1.5rem" className={className} />),
    description: 'Master contract editor',
    title: 'links.contract.master.title',
    defaultTitle: 'Master Contract',
    links: []
  },
  [Map]: {
    icon: (className?: string) => (<Icon path={mdiMapOutline} size="1.5rem" className={className} />),
    description: 'Map',
    title: 'links.map',
    defaultTitle: 'Map',
    links: [Dashboard]
  },
  [MessageManager]: {
    icon: (className?: string) => (<Icon path={mdiForumOutline} size="1.5rem" className={className} />),
    description: 'Messages',
    title: 'links.message-manager',
    defaultTitle: 'Messages',
    links: [Dashboard]
  },
  [OrderManager]: {
    icon: (className?: string) => (<Icon path={mdiPackageVariantClosed} size="1.5rem" className={className} />),
    description: 'Orders',
    title: 'links.order-manager',
    defaultTitle: 'Orders',
    links: defaultLinks
  },
  [Profile]: {
    icon: (className?: string) => (<Icon path={mdiBadgeAccountOutline} size="1.5rem" className={className} />),
    description: 'Profile',
    title: 'links.profile',
    defaultTitle: 'Profile',
    links: defaultLinks
  },
  [ProviderManager]: {
    icon: (className?: string) => (<Icon path={mdiHandshakeOutline} size="1.5rem" className={className} />),
    description: 'Providers',
    title: 'links.provider-manager',
    defaultTitle: 'Providers',
    links: defaultLinks
  },
  [Settings]: {
    icon: (className?: string) => (<Icon path={mdiCogOutline} size="1.5rem" className={className} />),
    description: 'Settings',
    title: 'links.settings',
    defaultTitle: 'Settings',
    links: defaultLinks
  },
  [AccountManager]: {
    icon: (className?: string) => (<Icon path={mdiAccountMultiple} size="1.5rem" className={className} />),
    description: 'Manage user accounts',
    title: 'links.account.explorer',
    defaultTitle: 'User Management',
    roles: [EnumRole.ADMIN],
    links: [Dashboard],
  },
  [DraftManager]: {
    icon: (className?: string) => (<Icon path={mdiTextBoxCheckOutline} size="1.5rem" className={className} />),
    description: 'Manage provider draft assets',
    title: 'links.draft.explorer',
    defaultTitle: 'Draft Management',
    links: [Dashboard],
  },
  // Dynamic
  [AccountCreate]: {
    description: 'Create new account record',
    title: 'links.account.create',
    defaultTitle: 'Create Account',
    roles: [EnumRole.ADMIN],
    links: defaultLinks,
  },
  [AccountUpdate]: {
    description: 'Update account record',
    title: 'links.account.update',
    defaultTitle: 'Update Account',
    roles: [EnumRole.ADMIN],
    links: defaultLinks,
  },
  // Error Pages
  [Unauthorized]: {
    description: 'Unauthorized',
  },
  [Forbidden]: {
    description: 'Forbidden',
  },
  [NotFound]: {
    description: 'Not Found',
  },
  [InternalServerError]: {
    description: 'Internal Server Error',
  },
};

export interface RouteMatch {
  path: string,
  route: string,
  properties: Route,
  params: string[],
}
/**
 * Matches the given path to an existing route and returns the route or null
 * if no match is found
 *
 * @export
 * @param {any} path - the route path to match
 * @returns an object with the initial path, the route that matched the given path, the route properties
 * and an array of the parameters. If no match if found, null is returned
 */
export function matchRoute(path: string): RouteMatch | null {
  for (let route in routes) {
    let re = pathToRegexp.pathToRegexp(route);
    const result = re.exec(path);
    if (result) {
      return {
        path,
        route,
        properties: routes[route],
        params: result.splice(1, result.length),
      };
    }
  }

  return null;
}


/**
 * Find a route by its path e.g. /Dashboard
 *
 * @export
 * @param {string} path - the route path
 * @returns the route properties
 */
export function getRoute(path: string): Route | null {
  const match = matchRoute(path);

  if (match) {
    return {
      ...routes[match.route],
      params: match.params,
    };
  }
  return null;
}

/**
 * Build a path given a route and optional parameters
 *
 * @export
 * @param {string} path - The route name
 * @param {string[]|object} params - Optional parameters to bind
 */
export function buildPath(path: string, params: string[] | { [key: string]: string }) {
  let result = path || '/';

  if (params) {
    if (Array.isArray(params)) {
      let re = /:\w+/i;
      for (const value of params) {
        result = result.replace(re, value);
      }
    } else {
      let toPath = pathToRegexp.compile(path);
      result = toPath(params);
    }
  }
  return result;
}
