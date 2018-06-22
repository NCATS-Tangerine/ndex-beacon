package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.BeaconStatement;
import bio.knowledge.server.model.BeaconStatementWithDetails;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Controller
public class StatementsApiController implements StatementsApi {

	@Autowired ControllerImpl ctrl; 

    public ResponseEntity<BeaconStatementWithDetails> getStatementDetails(@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought ",required=true ) @PathVariable("statementId") String statementId,
         @ApiParam(value = "an array of keywords or substrings against which to  filter annotation names (e.g. publication titles).") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size) {
        return ctrl.getStatementDetails(statementId, keywords, size);
    }

    public ResponseEntity<List<BeaconStatement>> getStatements( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). ", required = true) @RequestParam(value = "s", required = true) List<String> s,
         @ApiParam(value = "(Optional) A predicate edge label against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate edge_names for this parameter should be as published by the /predicates API endpoint and must be taken from the minimal predicate ('slot') list of the [Biolink Model](https://biolink.github.io/biolink-model). ") @RequestParam(value = "edgeLabel", required = false) String edgeLabel,
         @ApiParam(value = "(Optional) A predicate relation against which to constrain the search for statements ('edges') associated with the given query seed concept. The predicate relations for this parameter should be as published by the /predicates API endpoint and the preferred format is a CURIE  where one exists, but strings/labels acceptable. This relation may be equivalent to the edge_label (e.g. edge_label: has_phenotype, relation: RO:0002200), or a more specific relation  in cases where the source provides more granularity (e.g. edge_label: molecularly_interacts_with, relation: RO:0002447) ") @RequestParam(value = "relation", required = false) String relation,
         @ApiParam(value = "(optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). ") @RequestParam(value = "t", required = false) List<String> t,
         @ApiParam(value = "an array of keywords or substrings against which to filter concept names and synonyms") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of concept entries requested by the client; if this  argument is omitted, then the query is expected to returned all  the available data for the query ") @RequestParam(value = "size", required = false) Integer size) {
        return ctrl.getStatements(s, edgeLabel, relation, t, keywords, categories, size);
    }

}
