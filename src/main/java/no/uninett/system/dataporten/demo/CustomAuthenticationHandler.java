package no.uninett.system.dataporten.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

public class CustomAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private RequestCache requestCache = new HttpSessionRequestCache();

	private String nonApprovedDefaultTargetUrl;
	private String approvedDefaultTargetUrl;;

	public String getApprovedDefaultTargetUrl() {
		return approvedDefaultTargetUrl;
	}

	public void setApprovedDefaultTargetUrl(String approvedDefaultTargetUrl) {
		this.approvedDefaultTargetUrl = approvedDefaultTargetUrl;
	}

	public String getNonApprovedDefaultTargetUrl() {
		return nonApprovedDefaultTargetUrl;
	}

	public void setNonApprovedDefaultTargetUrl(String nonApprovedDefaultTargetUrl) {
		this.nonApprovedDefaultTargetUrl = nonApprovedDefaultTargetUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	    Authentication authentication) throws ServletException, IOException {
		String role = authentication.getAuthorities().isEmpty() ? null : authentication.getAuthorities().toArray()[0]
		    .toString();
		if (role == null) { // non-approved user
			setDefaultTargetUrl(nonApprovedDefaultTargetUrl);
			requestCache.removeRequest(request, response);
		} else {
			setDefaultTargetUrl(approvedDefaultTargetUrl);
		}

		// Pass some values to non-secured pages, authentication is null on
		// non-secured pages even after login
		Map<String, String> userInfo = new HashMap<>();
		HttpSession httpSession = request.getSession(true);
      OAuth2Authentication oauth2auth = (OAuth2Authentication)authentication;
		User user = (User) oauth2auth.getUserAuthentication().getDetails();
		userInfo.put("ID", user.getFeideId());
		userInfo.put("CN", user.getName());
		userInfo.put("Email", user.getEmail());
		httpSession.setAttribute("SPRING_SECURITY_CONTEXT_USERINFO", userInfo);
		super.onAuthenticationSuccess(request, response, authentication);
	}

   @Override
	public void setRequestCache(RequestCache requestCache) {
		this.requestCache = requestCache;
	}

}