import { API_GATEWAY, API_ENDPOINT, AUTH_REQUEST } from './FetchWrapper';
import queryString from 'query-string';

export default class Auth {
  accessToken = null;
  expiresAt = null;

  constructor() {
    this.login = this.login.bind(this);
    this.handleAuthentication = this.handleAuthentication.bind(this);
    this.isAuthenticated = this.isAuthenticated.bind(this);
  }

  login() {
    var url = API_GATEWAY + API_ENDPOINT + AUTH_REQUEST;
    window.location.href = url;
  }

  handleAuthentication() {
    const authResult = queryString.parse(window.location.search);
    if(authResult && authResult.access_token && authResult.expires_in) {
      // Set isLoggedIn flag in localStorage
      localStorage.setItem('isLoggedIn', 'true');

      // Set the time that the access token will expire at
      this.expiresAt = (parseInt(authResult.expires_in) * 1000) + new Date().getTime();
      this.accessToken = authResult.access_token;
console.log(this.accessToken);

      localStorage.setItem('accessToken', this.accessToken);
    } else {
      console.log("An error occurred while authentication.");
      alert(`Error: Check the console for further details.`);
    }
  }

  isAuthenticated() {
    return (this.expiresAt && ((new Date()).getTime() < this.expiresAt))
  }
}
