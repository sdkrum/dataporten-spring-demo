package no.uninett.system.dataporten.demo;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {

  private static final long serialVersionUID = 1L;

  private String username;
  private String password;
  private Collection<? extends GrantedAuthority> authorities; // Global level ?
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;
  private boolean enabled;
  private boolean approved;

  // added
  private String name;

  private String feideId;
  private String firstname;
  private String lastname;
  private String email;
  private String orgshortname;
  private String accessToken;
  private boolean isAdmin;

  public User(Collection<? extends GrantedAuthority> authorities, boolean accountNonExpired, boolean accountNonLocked,
      boolean credentialsNonExpired, boolean enabled, String firstname, String lastname, String email, String orgshortname, String feideId,
      boolean isAdmin) {
    this.authorities = authorities;
    this.username = feideId;
    this.password = null;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.orgshortname = orgshortname;
    this.feideId = feideId;
    this.setIsAdmin(isAdmin);
    this.approved = !authorities.isEmpty();
  }

  public User() {
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public String getName() {
    return this.firstname + " " + this.lastname;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFeideId() {
    return feideId;
  }

  public void setFeideId(String feideId) {
    this.feideId = feideId;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOrgshortname() {
    return orgshortname;
  }

  public void setOrgshortname(String orgshortname) {
    this.orgshortname = orgshortname;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append("[");
    for (GrantedAuthority a : authorities) {
      sb.append(a.getAuthority());
      sb.append(" ,");
    }
    sb.append("]");
    return sb.toString().replace(" ,]", "]");
  }

  public void setAuthorities(List<SimpleGrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  public final void setIsAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }

  public boolean getIsAdmin() {
    return isAdmin;
  }

  public void setApproved(boolean approved) {
    this.approved = approved;
  }

  public boolean isApproved() {
    return approved;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
