package bio.knowledge.server.api;

import bio.knowledge.server.model.BeaconStatement;

import io.swagger.annotations.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Controller
public class StatementsApiController implements StatementsApi {



    public ResponseEntity<List<BeaconStatement>> getStatements( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of  'source' concepts possibly known to the beacon. Unknown CURIES should simply be ignored (silent match failure). ", required = true) @RequestParam(value = "s", required = true) List<String> s,
         @ApiParam(value = "an array set of strings of Biolink predicate relation name labels against which to constrain the search for statement relations associated with the given query seed concept. The predicate  relation names for this parameter should be as published by  the beacon-aggregator by the /predicates API endpoint as taken from the minimal predicate list of the Biolink Model  (see [Biolink Model](https://biolink.github.io/biolink-model)  for the full list of predicates). ") @RequestParam(value = "relations", required = false) List<String> relations,
         @ApiParam(value = "(optional) an array set of [CURIE-encoded](https://www.w3.org/TR/curie/) identifiers of 'target' concepts possibly known to the beacon.  Unknown CURIEs should simply be ignored (silent match failure). ") @RequestParam(value = "t", required = false) List<String> t,
         @ApiParam(value = "an array of keywords or substrings against which to filter concept names and synonyms") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "an array set of concept categories (specified as Biolink name labels codes gene, pathway, etc.) to which to constrain concepts matched by the main keyword search (see [Biolink Model](https://biolink.github.io/biolink-model) for the full list of codes) ") @RequestParam(value = "categories", required = false) List<String> categories,
         @ApiParam(value = "maximum number of statement entries requested by the query (default 100) ") @RequestParam(value = "size", required = false) Integer size) {
        // do some magic!
        return new ResponseEntity<List<BeaconStatement>>(HttpStatus.OK);
    }

}
