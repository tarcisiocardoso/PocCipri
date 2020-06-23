import Auth from "./Auth";

export const API_GATEWAY = 'https://wso2prodf:8243';
export const API_ENDPOINT = '/semp/service/0.0.1';
export const AUTH_REQUEST = '/login/authorize'

export default class FetchWrapper {
  static auth = new Auth();

  static getAuth() {
    return FetchWrapper.auth;
  }

  static doGet(url, headers) {
    let token = null;
    if (!FetchWrapper.auth.isAuthenticated()) {
      FetchWrapper.auth.login();
      return Promise.reject("Usuário não autenticado.");
    } else {
      token = localStorage.getItem('accessToken');
    }

    let uri = API_GATEWAY + API_ENDPOINT + url;
    let params = {
      method: 'GET',
      headers: headers
    }
    if (!params.headers) {
      params.headers = {
        'Accept': 'application/json'
      }
    } else if (!params.headers['Accept']) {
      params.headers['Accept'] = 'application/json';
    }
    params.headers.Authorization = 'Bearer ' + token;

    return new Promise((resolve, reject) => {
      fetch(uri, params)
        .then(async response => {
          if (response.ok) {
            resolve(response);
          } else {
            reject(response);
          }
        })
        .catch(error => {
          throw new Error(error);
        });
    });
  }
}
