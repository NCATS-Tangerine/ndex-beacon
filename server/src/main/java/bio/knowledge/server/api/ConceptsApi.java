package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.BeaconConcept;
import bio.knowledge.server.model.BeaconConceptWithDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Api(value = "concepts", description = "the concepts API")
public interface ConceptsApi {

    @ApiOperation(value = "", notes = "Retrieves details for a specified concepts in the system, as specified by a (url-encoded) CURIE identifier of a concept known the given knowledge source. ", response = BeaconConceptWithDetails.class, tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with concept details returned ", response = BeaconConceptWithDetails.class) })
    @RequestMapping(value = "/concepts/{conceptId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<BeaconConceptWithDetails> getConceptDetails(@ApiParam(value = "(url-encoded) CURIE identifier of concept of interest",required=true ) @PathVariable("conceptId") String conceptId);


    @ApiOperation(value = "", notes = "Retrieves a list of whose concept in the  beacon knowledge base with names and/or synonyms  matching a set of keywords or substrings.  The results returned should generally  be returned in order of the quality of the match,  that is, the highest ranked concepts should exactly  match the most keywords, in the same order as the  keywords were given. Lower quality hits with fewer  keyword matches or out-of-order keyword matches,  should be returned lower in the list. ", response = BeaconConcept.class, responseContainer = "List", tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with list of core concept data returned ", response = BeaconConcept.class) })
    @RequestMapping(value = "/concepts",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<BeaconConcept>> getConcepts( @NotNull @ApiParam(value = "an array of keywords or substrings against which to match concept names and synonyms", required = true) @RequestParam(value = "keywords", required = true) List<String> keywords,
         @ApiParam(value = "an array set of concept categories - specified as Biolink name labels codes gene, pathway, etc. - to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of terms) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size);

}
