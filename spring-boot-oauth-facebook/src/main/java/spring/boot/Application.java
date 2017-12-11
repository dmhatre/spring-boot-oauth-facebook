package spring.boot;

import java.security.Principal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@SpringBootApplication
@EnableOAuth2Sso
public class Application extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		Object[] obj = {Application.class};
		SpringApplication.run(obj, args);
	}

	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher("/**")
		.authorizeRequests().antMatchers("/","/login**","/webjars/**","/logout")
		.permitAll().anyRequest().authenticated()
		.and().logout().logoutSuccessUrl("/").permitAll();
	}
	@RequestMapping(value = "/")
	public ModelAndView homepage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping(value = "/homepage")
	public ModelAndView homepagenew(HttpServletRequest request) {

		System.out.println("**--"+request.getUserPrincipal().getName()+"--**");
		for(Cookie c: request.getCookies()) {
			System.out.println(c.getName()+"-"+c.getValue());
		}
	
		ModelAndView mv = new ModelAndView();
		mv.setViewName("homepage");
		return mv;
	}

	@RequestMapping("/user")
	public Principal user(HttpServletRequest request, Principal principal) {
		
		DefaultOAuth2ClientContext test = (DefaultOAuth2ClientContext) request.getSession().getAttribute("scopedTarget.oauth2ClientContext");

		String accessToken = test.getAccessToken().getValue();

		FacebookTemplate template = new FacebookTemplate(accessToken); 
		String [] fields = { "id", "email",  "first_name", "last_name" };
		User profile = template.fetchObject("me", User.class, fields);
		System.out.println("-+"+profile.getFirstName());
		System.out.println("-+"+profile.getLastName());
		System.out.println("-+"+profile.getBirthday());
		System.out.println("-+"+profile.getEmail());
		System.out.println("-+"+profile.getId());

		return principal;	
	}
}