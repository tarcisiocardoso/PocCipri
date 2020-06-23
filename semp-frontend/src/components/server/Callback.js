import React from 'react';
import { withRouter } from 'react-router-dom';

class Callback extends React.Component {
  componentDidMount() {
    this.props.auth.handleAuthentication();
    this.props.history.replace('/');
  }

  render() {
    return (
      <p>Loading...</p>
    )
  }
}

export default withRouter(Callback);
