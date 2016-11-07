package no.uninett.system.dataporten.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException, ServletException {
    if (authentication != null) {
      OAuth2Authentication oauth2auth = (OAuth2Authentication) authentication;
      User user = (User) oauth2auth.getUserAuthentication().getDetails();
      // do your stuff here...
    }
    super.onLogoutSuccess(request, response, authentication);
  }
}
