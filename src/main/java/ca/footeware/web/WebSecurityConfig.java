/*******************************************************************************
 * Copyright (c) 2016 Footeware.ca
 *******************************************************************************/
package ca.footeware.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.firewall.StrictHttpFirewall;

/**
 * Specify the URLs to which the current user can access and those that are
 * forwarded to login.
 *
 * @author Footeware.ca
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.httpBasic().and().authorizeRequests().antMatchers().permitAll().anyRequest().authenticated().and()
				.formLogin().loginPage("/login").permitAll().and().logout().permitAll();
		httpSecurity.headers().frameOptions().disable();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		super.configure(web);
		web.ignoring().antMatchers("/", "/gear/**", "/jokes/**", "/addjoke/**", "/editjoke/**", "/deletejoke/**",
				"/styles/**", "/js/**", "/images/**", "/fonts/**", "/gallery", "/gallery/Artsy-Fartsy/",
				"/gallery/Artsy-Fartsy/**", "/gallery/thumbnails/Artsy-Fartsy/**");
		StrictHttpFirewall firewall = new StrictHttpFirewall();
		firewall.setAllowUrlEncodedPercent(true);
		web.httpFirewall(firewall);
	}

	/**
	 * @param auth {@link AuthenticationManagerBuilder}
	 * @throws Exception when shit goes south
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		String password = encoder.encode("bogie97");
		UserDetails user = User.withUsername("foote").password(password).roles("USER").build();
		auth.inMemoryAuthentication().withUser(user);
	}

}