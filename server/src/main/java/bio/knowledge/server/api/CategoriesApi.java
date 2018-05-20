package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bio.knowledge.server.model.BeaconConceptCategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Api(value = "categories", description = "the categories API")
public interface CategoriesApi {

    @ApiOperation(value = "", notes = "Get a list of concept categories and number of their concept instances documented by the knowledge source. These types should be mapped onto the Translator-endorsed Biolink Model concept type classes with local types, explicitly added as mappings to the Biolink Model YAML file.  ", response = BeaconConceptCategory.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with categories and frequency returned ", response = BeaconConceptCategory.class) })
    @RequestMapping(value = "/categories",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<BeaconConceptCategory>> getConceptCategories();

}
