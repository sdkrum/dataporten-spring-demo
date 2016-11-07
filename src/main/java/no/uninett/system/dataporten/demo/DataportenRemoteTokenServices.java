package no.uninett.system.dataporten.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import no.norstore.archive.webui.session.entity.User;
//import no.norstore.archive.webui.util.ResourceUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Request;
import static org.springframework.security.oauth2.provider.token.AccessTokenConverter.CLIENT_ID;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

/**
 *
 * @author sorenk
 */
public class DataportenRemoteTokenServices implements ResourceServerTokenServices {

  // just for demo, figure out/place your roles as you wish...
  private final static String[] ADMINS = {"admin1@feide.no", "admin2@feide.no"};
  private final static int TIMEOUT_IN_MILLI = 1000 * 60 * 15;

  private final String clientId;
  private final String userEndpointUrl;

  public DataportenRemoteTokenServices(String clientId, String userEndpointUrl) {
    this.clientId = clientId;
    this.userEndpointUrl = userEndpointUrl;
  }

  @Override
  public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    if (accessToken != null && !"".equals(accessToken)) {
      try {
        // the following is not totally exact, but probably good enough.
        Date expiration = new Date(System.currentTimeMillis() + (TIMEOUT_IN_MILLI));
        OkHttpClient client = new OkHttpClient();
        Request request = getUserInfoRequest(accessToken);
        Response response = client.newCall(request).execute();
        Gson gSon = new GsonBuilder().create();
        OauthAuth auth = gSon.fromJson(response.body().string(), OauthAuth.class);
        if (!clientId.equals(auth.audience)) {
          // What? Something bad happend! probably a sec breach...
          throw new AuthenticationServiceException("Security breach! The client id does not match the audience");
        }
        User userDetails = getUser(auth);
        userDetails.setAccessToken(accessToken);
        //TODO check if user is in DB, if not create him
        //Object principal = userDetails;
        Object credential = null;
        ExpiringUsernameAuthenticationToken authentication = new ExpiringUsernameAuthenticationToken(expiration, accessToken, credential, userDetails.getAuthorities());
        authentication.setDetails(userDetails);
        Map<String, String> parameters = new HashMap<>();
        parameters.put(CLIENT_ID, auth.audience);
        OAuth2Request storedRequest = new OAuth2Request(parameters, this.clientId, userDetails.getAuthorities(), true, null, null, null, null, null);
        OAuth2Authentication oauth = new OAuth2Authentication(storedRequest, authentication);
        return oauth;
      } catch (IOException e) {
        throw new InvalidTokenException("Unable to load token", e);
      }
    } else {
      throw new InvalidTokenException("Token is null");
    }
  }

  private Request getUserInfoRequest(String accessToken) {
    Request request = new Request.Builder()
      .url(userEndpointUrl)
      .get()
      .addHeader("Authorization", getAuthorizationHeader(accessToken))
      .addHeader("cache-control", "no-cache")
      .build();
    return request;
  }

  private String getAuthorizationHeader(String token) {
    return String.format("Bearer %s", token);
  }

  private User getUser(OauthAuth auth) {
    String feideId = getFeide(auth);
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    boolean isAdmin = false;
    for (String admin : ADMINS) {
      if (admin.toLowerCase().equals(feideId.toLowerCase())) {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        authorities.add(grantedAuthority);
        isAdmin = true;
        break;
      }
    }
    // boolean enabled = true;
    if (!isAdmin) {
      SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
      authorities.add(grantedAuthority);
    }
    String[] names = auth.user.name.split(" ");
    String first = "";
    String space = "";
    for (int i = 0; i < names.length - 1; i++) {
      first += space + names[i];
      space = " ";
    }
    String last = names[names.length - 1];
    User userDetails = new User(
      authorities,
      true, // accountNonExpired,
      true, // accountNonLocked,
      false, // credentialsNonExpired,
      true, // enabled, //false -> no difference , sometime authentication
      // == null even authenticated
      first, last,
      auth.user.email,
      null, // TODO how to get the the Org short name?
      feideId, isAdmin);
    return userDetails;
  }

  private String getFeide(OauthAuth auth) {
    String id = auth.user.userid_sec.iterator().next();
    // something like "feide:<feide-id>"
    if (id.startsWith("feide:")) {
      id = id.substring("feide:".length());
    }
    return id;
  }

  @Override
  public OAuth2AccessToken readAccessToken(String accessToken) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  class OauthUser {

    Set<String> userid_sec;
    String userid;
    String name;
    String email;

  }

  class OauthAuth {

    OauthUser user;
    String audience;
  }
}
