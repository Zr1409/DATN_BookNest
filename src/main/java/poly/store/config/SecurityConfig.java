
package poly.store.config;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import poly.store.service.CustomOAuth2UserService;
import poly.store.service.UserService;
import poly.store.service.impl.ShoppingCartServiceImpl;
import poly.store.service.impl.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	// Thong tin User Service
	@Autowired
	UserService userService;

	// Phuong thuc cap quyen
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	// Thong tin datasource
	@Autowired
	DataSource dataSource;

	@Autowired
	ShoppingCartServiceImpl shoppingCartServiceImpl;

	@Autowired
	private CustomOAuth2UserService customOAuth2UserService;

	/**
	 * Cung cap quyen cho project
	 * 
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	/**
	 * Xu ly phan quyen nguoi dung
	 * 
	 * @param http
	 * @throws exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		// Cac trang yeu cau quyen su dung la Admin hoac Director
		http.authorizeRequests().antMatchers("/admin/**").access("hasAnyRole('ROLE_ADMIN', 'ROLE_DIRECTOR')");

		http.authorizeRequests().antMatchers("/shop/profile/**", "/shop/favorite/**", "/shop/cart/checkout", "/account",
				"/account/**", "/rest/favorite/add/**")
				.access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_DIRECTOR')");

		// Các trang không yêu cầu login
		http.authorizeRequests().anyRequest().permitAll();

		// Khi người dùng đã login, với vai trò XX.
		// Nhưng truy cập vào trang yêu cầu vai trò YY,
		// Ngoại lệ AccessDeniedException sẽ ném ra.
		// http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403page");

		// Cau hinh cho form login
		http.authorizeRequests().and().formLogin().loginPage("/login").usernameParameter("username")
				.passwordParameter("password").failureForwardUrl("/login").defaultSuccessUrl("/login/success", false);

		// Cau hinh dang xuat khoi chuong trinh
		http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/index")
				.invalidateHttpSession(true) // Hủy session khi đăng xuất
				.clearAuthentication(true) // Xóa thông tin xác thực khi đăng xuất
				.addLogoutHandler((request, response, authentication) -> {
					// Xóa giỏ hàng trong session
					HttpSession session = request.getSession(false);
					if (session != null) {
						session.removeAttribute("cart"); // Xóa giỏ hàng trong session
					}
					// Xóa giỏ hàng trong ShoppingCartServiceImpl
					shoppingCartServiceImpl.clear();
				}).permitAll();

		// Cau hinh remember me
		http.authorizeRequests().and().rememberMe().tokenValiditySeconds(86400);

		http.oauth2Login().loginPage("/login").userInfoEndpoint().userService(customOAuth2UserService).and()
				.defaultSuccessUrl("/login/success", true); // ✅ fix tại đây
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(googleClientRegistration());
	}

	private ClientRegistration googleClientRegistration() {
		return ClientRegistration.withRegistrationId("google").clientId(System.getenv("GOOGLE_CLIENT_ID"))
				.clientSecret(System.getenv("GOOGLE_CLIENT_SECRET")).scope("profile", "email")
				.authorizationUri("https://accounts.google.com/o/oauth2/auth")
				.tokenUri("https://oauth2.googleapis.com/token")
				.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUriTemplate("{baseUrl}/login/oauth2/code/{registrationId}").userNameAttributeName("email")
				.build();
	}

	/**
	 * Cung cap phuong thuc ma hoa
	 * 
	 * @return phuong thuc ma hoa
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}
}
