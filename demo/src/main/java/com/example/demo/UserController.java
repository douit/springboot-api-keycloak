package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequestMapping(value = "/users")
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private String authServerUrl = "http://localhost:8080/auth";
    private String realm = "realm-1";
    private String clientId = "client-1";
    private String role1 = "pleb";
    private String role2 = "pleb1";
    // Get client secret from the Keycloak admin console (in the credential tab)
    private String clientSecret = "ff0634c2-039e-4baf-960d-790df88d3cde";

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(path = "/create")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDTO) {

        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(authServerUrl).grantType(OAuth2Constants.PASSWORD)
                .realm("master").clientId("admin-cli").username("admin").password("963456")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

        keycloak.tokenManager().getAccessToken();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstname());
        user.setLastName(userDTO.getLastname());
        user.setEmail(userDTO.getEmail());

        // Get realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersRessource = realmResource.users();

        Response response = usersRessource.create(user);

        userDTO.setStatusCode(response.getStatus());
        userDTO.setStatus(response.getStatusInfo().toString());

        if (response.getStatus() == 201) {

            String userId = CreatedResponseUtil.getCreatedId(response);

            log.info("Created userId {}", userId);

            // create password credential
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userDTO.getPassword());

            UserResource userResource = usersRessource.get(userId);

            // Set password credential
            userResource.resetPassword(passwordCred);

            // Get realm role student
            RoleRepresentation realmRoleUser = realmResource.roles().get(role1).toRepresentation();

            // Assign realm role student to user
            userResource.roles().realmLevel().add(Arrays.asList(realmRoleUser));
        }
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<?> signin(@RequestBody UserDto userDTO) {

        Map<String, Object> clientCredentials = new HashMap<>();
        clientCredentials.put("secret", clientSecret);
        clientCredentials.put("grant_type", "password");

        Configuration configuration = new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);

        AccessTokenResponse response = authzClient.obtainAccessToken(userDTO.getEmail(), userDTO.getPassword());

        return ResponseEntity.ok(response);
    }

    // @PostMapping(path = "/signout")
    // public ResponseEntity<?> signout(HttpServletRequest request,@RequestBody
    // LogOutRequest logOutRequest) {
    // try {
    // String url =
    // authServerUrl+"/realms/"+realm+"/protocol/openid-connect/logout";

    // HttpHeaders headers = new HttpHeaders();
    // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    // headers.set("Authorization", request.getHeader("Authorization"));
    // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));

    // Map<String, Object> map = new HashMap<>();
    // map.put("client_id", clientId);
    // map.put("refresh_token", logOutRequest.getRefreshToken());

    // HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
    // this.restTemplate.postForObject(url, entity, LogOutRequest.class);

    // return ResponseEntity.ok("Log out success");
    // } catch (Exception e) {
    // e.printStackTrace();
    // return (ResponseEntity<?>) ResponseEntity.badRequest();
    // }
    // }

    @GetMapping(value = "/unprotected-data")
    public String getName() {
        return "Hello, this api is not protected.";
    }

    @PreAuthorize("hasRole('ROLE_pleb')")
    @GetMapping(value = "/for-Pleblv1")
    public ResponseEntity<?> getEmail() {
        try {
            String url = "http://38b31f3d4565.ngrok.io/menu/getMenuById?menuId=1";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // headers.setAccept(Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED));

            // Map<String, Object> map = new HashMap<>();
            // map.put("keySearch", "");
            // map.put("limit", 100);
            // map.put("pageNumber", 1);
            // map.put("pageShow", 5);
            // map.put("size", 2);
            // map.put("sortType", "asc");
            // map.put("sortValue", "orders");

            // HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
            MenuDto menuDto = this.restTemplate.getForObject(url, MenuDto.class);
            return ResponseEntity.ok(menuDto);
        } catch (Exception e) {
            return (ResponseEntity<?>) ResponseEntity.badRequest();
        }
    }

    @PreAuthorize("hasRole('ROLE_pleb1')")
    @GetMapping(value = "/for-Pleblv2")
    public ResponseEntity<?> getWelp() {
        return ResponseEntity.ok("Log out success");
    }
}
