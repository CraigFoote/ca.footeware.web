package ca.footeware.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Specify the resource folders from which to get images, etc.
 *
 * @author Footeware.ca
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

	@Value("${images.path}")
	private String imagesPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/static/**").addResourceLocations("file:" + imagesPath);
	}
}