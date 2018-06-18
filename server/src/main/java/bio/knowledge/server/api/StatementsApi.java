package bio.knowledge.server.api;

import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.BeaconStatementWithDetails;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Api(value = "statements", description = "the statements API")
public interface StatementsApi {

    @ApiOperation(value = "", notes = "Retrieves a details relating to a specified concept-relationship statement include 'is_defined_by and 'provided_by' provenance; extended edge properties exported as tag = value; and any associated annotations (publications, etc.)  cited as evidence for the given statement. ", response = BeaconStatementWithDetails.class, tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "successful call with statement details returned ", response = BeaconStatementWithDetails.class) })
    @RequestMapping(value = "/statements/{statementId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<BeaconStatementWithDetails> getStatementDetails(@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought ",required=true ) @PathVariable("statementId") String statementId,
         @ApiParam(value = "an array of keywords or substrings against which to  filter annotation names (e.g. publication titles).") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size);


    @ApiOperation(value = "", notes = "Given a specified set of [CURIE-encoded](https://www.w3.org/TR/curie/)  source ('s') concept identifiers,  retrieves a list of relationship statements where either the subject or object concept matches any of the input 'source' concepts provided.  Optionally, a set of target ('t') concept  identifiers may also be given, in which case a member of the 'target' identifier set should match the concept opposing the 'source' in the  statement, that is, if the'source' matches a subject, then the  'target' should match the object of a given statement (or vice versa). ", response = BeaconStatement.class, responseContainer = "List", tags={ "statements", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response returns a list of concept-relations where there is an exact match of an input concept identifier either to the subject or object concepts  of the statement ", response = BeaconStatement.class) })
    @RequestMapping(value = "/statements",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<BeaconStatement>> getStatements( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). ", required = true) @RequestParam(value = "s", required = true) List<String> s,
         @ApiParam(value = "(Optional) A predicate edge label against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate edge_names for this parameter should be as published by the /predicates API endpoint and must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). ") @RequestParam(value = "edgeLabel", required = false) String edgeLabel,
         @ApiParam(value = "(Optional) A predicate relation against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate relations for this parameter should be as published by the /predicates API endpoint and the preferred format is a CURIE  where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation  in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447) ") @RequestParam(value = "relation", required = false) String relation,
         @ApiParam(value = "(optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). ") @RequestParam(value = "t", required = false) List<String> t,
         @ApiParam(value = "an array of keywords or substrings against which to filter concept names and synonyms") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size);

}
