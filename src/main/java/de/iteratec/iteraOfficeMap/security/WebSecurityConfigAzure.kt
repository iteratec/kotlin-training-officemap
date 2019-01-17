package de.iteratec.iteraOfficeMap.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("prod")
class WebSecurityConfigAzure
@Autowired constructor(private val oidcUserService: OAuth2UserService<OidcUserRequest, OidcUser>) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/login**", "/oauth2**").permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .oauth2Login()
                .loginPage("/oauth2/authorization/azure")
                .userInfoEndpoint()
                .oidcUserService(oidcUserService)

        http.csrf().disable()
    }

}
