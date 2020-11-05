import { createBrowserHistory } from 'history';

// A properly formatted basename should have a leading slash, but no trailing slash

export const history = createBrowserHistory({
  basename: '/helpdesk'
});
