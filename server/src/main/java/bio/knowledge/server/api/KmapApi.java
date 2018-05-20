package bio.knowledge.server.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-05-17T12:46:02.920-07:00")

@Api(value = "kmap", description = "the kmap API")
public interface KmapApi {

    @ApiOperation(value = "", notes = "Get a high level knowledge map of the all the beacons by subject semantic type, predicate and semantic object type ", response = BeaconKnowledgeMapStatement.class, responseContainer = "List", tags={ "metadata", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Successful response with categories and frequency returned ", response = BeaconKnowledgeMapStatement.class) })
    @RequestMapping(value = "/kmap",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<BeaconKnowledgeMapStatement>> getKnowledgeMap();

}
