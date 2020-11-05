# Requirements

Installing [Node.js](https://nodejs.org/en/) is required only for local development. Maven [frontend-maven-plugin](https://github.com/eirslett/frontend-maven-plugin) plugin will download automatically the appropriate Node.js version when building the project.

The application builds the client using node version `v10.16.3`. It is recommended using the [Node Version Manager](https://github.com/nvm-sh/nvm) for managing the installed node version:

```bash
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash
nvm install v10.16.3
nvm use v10.16.3
```

# Configure development environment

Before running the app in development mode, update `.env.development` file. If the default configuration options are used for the web application, no changes are required.

```properties
# Application title
REACT_APP_TITLE=OpertusMundi Helpdesk Web Client

# API url used by webpack dev server proxy
REACT_APP_ROXY_SERVER=http://localhost:9080

# Enable/Disable redux action logger
REACT_APP_ENABLE_LOGGER=false

# Disable browser
BROWSER=none

# Development server port 
PORT=3000
```

# Before running

In the project directory, install dependencies with:

    npm install

# Available Scripts

In the project directory, you can run:

    npm start

Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.<br />
You will also see any lint errors in the console.

    `npm test`

Launches the test runner in the interactive watch mode.<br />
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

    npm run build

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.
