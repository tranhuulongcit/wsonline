package com.jponline.wsonline.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SecurityConfiguration bypass check security request
 * Author: LongTH10
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected  void configure(HttpSecurity http) throws Exception {
		//bypass security
		http.addFilter(new AuthenticationProcessingFilter()).authorizeRequests().antMatchers("/**").permitAll().anyRequest().permitAll();
	}
}
