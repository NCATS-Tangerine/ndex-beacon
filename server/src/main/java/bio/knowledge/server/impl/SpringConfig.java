package bio.knowledge.server.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configure Spring to not truncate at the final dot in a path.
 * e.g. prevents "/concepts/ABC.def:123" from being truncated to "/concepts/ABC"
 * 
 * @author Meera Godden
 *
 */
@Configuration
public class SpringConfig implements WebMvcConfigurer {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);
	}
	
}

