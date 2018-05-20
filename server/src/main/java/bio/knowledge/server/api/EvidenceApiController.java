package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.BeaconAnnotation;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Controller
public class EvidenceApiController implements EvidenceApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<BeaconAnnotation>> getEvidence(@ApiParam(value = "(url-encoded) CURIE identifier of the concept-relationship statement (\"assertion\", \"claim\") for which associated evidence is sought ",required=true ) @PathVariable("statementId") String statementId,
         @ApiParam(value = "an array of keywords or substrings against which to filter citation titles") @RequestParam(value = "keywords", required = false) List<String> keywords,
         @ApiParam(value = "maximum number of cited references requested by the query (default 100) ") @RequestParam(value = "size", required = false) Integer size) {
        // do some magic!
    	return ctrl.getEvidence(statementId, keywords, size);
    }

}
