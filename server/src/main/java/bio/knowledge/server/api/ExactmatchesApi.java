package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.ExactMatchResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Api(value = "exactmatches", description = "the exactmatches API")
public interface ExactmatchesApi {

    @ApiOperation(value = "", notes = "Given an input array of [CURIE](https://www.w3.org/TR/curie/) identifiers of known exactly matched concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch), retrieves the list of [CURIE](https://www.w3.org/TR/curie/) identifiers of additional concepts that are deemed by the given knowledge source to be exact matches to one or more of the input concepts **plus** whichever concept identifiers from the input list were specifically matched to  these additional concepts, thus giving the whole known set of equivalent concepts known to this particular knowledge source.  If an empty set is  returned, the it can be assumed that the given knowledge source does  not know of any new equivalent concepts matching the input set. The caller of this endpoint can then decide whether or not to treat  its input identifiers as its own equivalent set. ", response = ExactMatchResponse.class, responseContainer = "List", tags={ "concepts", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response returns a set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of concepts (with supporting evidence code and reference) matching at least one identifier in the input list of known exactly matched concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). Each concept identifier is returned with  the full list of any additional associated [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of exact match concepts known to the given Knowledge Source. ", response = ExactMatchResponse.class) })
    @RequestMapping(value = "/exactmatches",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<ExactMatchResponse>> getExactMatchesToConceptList( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). ", required = true) @RequestParam(value = "c", required = true) List<String> c);

}
