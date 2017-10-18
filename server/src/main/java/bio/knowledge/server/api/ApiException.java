package bio.knowledge.server.api;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-10-18T13:42:46.892-07:00")

public class ApiException extends Exception {
 
	private static final long serialVersionUID = 6551612326512809952L;
	
	private int code;
	
	public ApiException (int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
}
