package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Controller
public class ConceptsApiController implements ConceptsApi {

	@Autowired ControllerImpl ctrl; 

    public ResponseEntity<BeaconConceptWithDetails> getConceptDetails(@ApiParam(value = "(url-encoded) CURIE identifier of concept of interest",required=true ) @PathVariable("conceptId") String conceptId) {
        return ctrl.getConceptDetails(conceptId);
    }

    public ResponseEntity<List<BeaconConcept>> getConcepts( @NotNull @ApiParam(value = "an array of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) List<String> keywords,
         @ApiParam(value = "an array set of concept categories - specified as Biolink name labels codes gene, pathway, etc. - to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size) {
        return ctrl.getConcepts(keywords, categories, size);
    }

}
