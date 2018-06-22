package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.BeaconKnowledgeMapStatement;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T19:09:25.899Z")

@Controller
public class KmapApiController implements KmapApi {

	@Autowired ControllerImpl ctrl; 

    public ResponseEntity<List<BeaconKnowledgeMapStatement>> getKnowledgeMap() {
        return ctrl.getKnowledgeMap();
    }

}
