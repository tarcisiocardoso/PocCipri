import React from 'react';
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import './App.css';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import { NavigationBar } from './components/navigation/NavigationBar';
import Home from './components/Home';
import { About } from './components/About';
import { NoMatch } from './components/NoMatch';
import Callback from './components/server/Callback';
import Sidebar from './components/navigation/Sidebar';
import FetchWrapper from './components/server/FetchWrapper';

let auth = FetchWrapper.getAuth();

function App() {
  return (
    <React.Fragment>
      <Router>
        <NavigationBar />
        <Sidebar />

        <Switch>
          <Route exact path='/' component={ Home } />
          <Route path='/about' component={ About } />
          <Route exact path='/callback' render={ (props) => <Callback {...props} auth={auth} /> } />
          <Route component={ NoMatch } />
        </Switch>
      </Router>
    </React.Fragment>
  );
}

export default App;
