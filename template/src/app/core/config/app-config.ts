export const APP_CONFIG = {
  /** API Spring Boot (server.port=8082 dans application.properties) */
  apiUrl: 'http://localhost:8082/api',
  keycloak: {
    url: 'http://localhost:8180',
    realm: 'blood',
    clientId: 'blood-front'
  }
};
