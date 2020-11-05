import React from 'react';

import { connect, ConnectedProps } from 'react-redux';
import { Redirect, Route, Switch } from 'react-router-dom';

// 3rd party components
import { ToastContainer } from 'react-toastify';

// State
import { RootState } from 'store';

// Components
import Home from 'components/home';
import Login from 'components/login';
import ErrorPage from 'components/error-page';
import PlaceHolder from 'components/placeholder';

// Model
import { ErrorPages, Pages, StaticRoutes } from 'model/routes';

class ContentRoot extends React.Component<PropsFromRedux> {

  render() {
    let authenticated = (this.props.profile != null);
    let routes;

    if (!authenticated) {
      routes = (
        <Switch>
          {/* Handle errors first */}
          <Route path={ErrorPages.Unauthorized} render={() => <ErrorPage code={401} color={'secondary'} />} exact />
          <Route path={ErrorPages.Forbidden} render={() => <ErrorPage code={403} color={'secondary'} />} exact />
          <Route path={ErrorPages.NotFound} render={() => <ErrorPage code={404} color={'primary'} />} exact />
          <Route path={ErrorPages.InternalServerError} render={() => <ErrorPage code={500} color={'secondary'} />} exact />
          {/* Public paths */}
          <Route path={Pages.Login} component={Login} />
          <Route path={Pages.ResetPassword} component={PlaceHolder} />
          {/* Default redirect */}
          <Redirect push={true} to={Pages.Login} />
        </Switch>
      );
    } else {
      routes = (
        <Switch>
          {/* Handle errors first */}
          <Route path={ErrorPages.Unauthorized} render={() => <ErrorPage code={401} color={'secondary'} />} exact />
          <Route path={ErrorPages.Forbidden} render={() => <ErrorPage code={403} color={'secondary'} />} exact />
          <Route path={ErrorPages.NotFound} render={() => <ErrorPage code={404} color={'primary'} />} exact />
          <Route path={ErrorPages.InternalServerError} render={() => <ErrorPage code={500} color={'secondary'} />} exact />
          {/* 
            Redirect for authenticated users. Navigation after a successful login operation
            occurs after the component hierarchy is rendered due to state change and causes
            /error/404 to render 
          */}
          <Redirect from={Pages.Login} to={StaticRoutes.Dashboard} exact />
          <Redirect from={Pages.ResetPassword} to={StaticRoutes.Dashboard} exact />
          {/* Default component */}
          <Route path="/" component={Home} />
        </Switch>
      );
    }

    return (
      <div>
        {routes}
        <ToastContainer
          className="opertusmundi-toastify"
          position="top-right"
          autoClose={5000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          pauseOnHover
        />
      </div>
    );
  }
}

//
// Container component
//

const mapState = (state: RootState) => ({
  profile: state.security.profile,
});

const connector = connect(
  mapState,
);

type PropsFromRedux = ConnectedProps<typeof connector>


export default connector(ContentRoot)