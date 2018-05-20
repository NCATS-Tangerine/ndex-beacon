package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.model.BeaconStatement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Api(value = "statements", description = "the statements API")
public interface StatementsApi {

    @ApiOperation(value = "", notes = "Given a specified set of [CURIE-encoded](https://www.w3.org/TR/curie/)  'source' ('s') concept identifiers,  retrieves a paged list of relationship statements where either the subject or object concept matches any of the input 'source' concepts provided.  Optionally, a set of 'target' ('t') concept  identifiers may also be given, in which case a member of the 'target' identifier set should match the concept opposing the 'source' in the  statement, that is, if the'source' matches a subject, then the  'target' should match the object of a given statement (or vice versa). ", response = BeaconStatement.class, responseContainer = "List", tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response returns a list of concept-relations where there is an exact match of an input concept identifier either to the subject or object concepts of the statement ", response = BeaconStatement.class) })
    @RequestMapping(value = "/statements",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<BeaconStatement>> getStatements( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). ", required = true) @RequestParam(value = "s", required = true) List<String> s,
         @ApiParam(value = "an array set of strings of Biolink predicate relation name labels against which to constrain the search for statement relations associated with the given query seed concept. The predicate  relation names for this parameter should be as published by  the beacon-aggregator by the /predicates API endpoint as taken from the minimal predicate list of the Biolink Model  (see [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of predicates). ") @RequestParam(value = "relations", required = false) List<String> relations,
         @ApiParam(value = "(optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). ") @RequestParam(value = "t", required = false) List<String> t,
         @ApiParam(value = "an array of keywords or substrings against which to filter concept names and synonyms") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of statement entries requested by the query (default 100) ") @RequestParam(value = "size", required = false) Integer size);

}
