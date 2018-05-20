package bio.knowledge.server.api;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.ExactMatchResponse;
import io.swagger.annotations.ApiParam;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Controller
public class ExactmatchesApiController implements ExactmatchesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<ExactMatchResponse>> getExactMatchesToConceptList( @NotNull @ApiParam(value = "an array set of [CURIE-encoded](https://www.w3.org/TR/curie/)  identifiers of concepts thought to be exactly matching concepts, to be used in a search for additional exactly matching concepts [*sensa*-SKOS](http://www.w3.org/2004/02/skos/core#exactMatch). ", required = true) @RequestParam(value = "c", required = true) List<String> c) {
        // do some magic!
        //return new ResponseEntity<List<ExactMatchResponse>>(HttpStatus.OK);
        return ctrl.getExactMatchesToConceptList(c);
 
    }

}
