package bio.knowledge.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("nDex Bio Translator Knowledge Beacon API")
            .description("This is the Translator Knowledge Beacon web service application programming interface (API) wrapping the nDex Bio (ndexbio.org) bioinformatics graph data archive.. ")
            .license("MIT License")
            .licenseUrl("http://opensource.org/licenses/MIT")
            .termsOfServiceUrl("http://starinformatics.com")
            .version("1.1.0")
            .contact(new Contact("","", "richard@starinformatics.com"))
            .build();
    }

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("bio.knowledge.server.api"))
                    .build()
                .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class)
                .apiInfo(apiInfo());
    }

}
