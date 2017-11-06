package bio.knowledge.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import bio.knowledge.server.impl.ControllerImpl;
import bio.knowledge.server.model.Summary;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-05T07:12:50.191-08:00")

@Controller
public class TypesApiController implements TypesApi {

	@Autowired ControllerImpl ctrl;

    public ResponseEntity<List<Summary>> linkedTypes() {
         return ctrl.linkedTypes();
    }

}
