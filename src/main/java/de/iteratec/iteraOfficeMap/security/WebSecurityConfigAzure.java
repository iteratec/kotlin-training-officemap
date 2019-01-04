package de.iteratec.iteraOfficeMap.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("prod")
public class WebSecurityConfigAzure extends WebSecurityConfigurerAdapter {

    private final OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

    @Autowired
    public WebSecurityConfigAzure(OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) {
        this.oidcUserService = oidcUserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login**", "/oauth2**").permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/oauth2/authorization/azure")
                .userInfoEndpoint()
                .oidcUserService(oidcUserService);


//        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        // TODO: we should test how to fix this for real (when we have the time)
        http.csrf().disable();
    }

}

