package org.raspinloop.orchestrator.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-03-09T06:25:46.920Z[GMT]")
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends ApiException {
    private int code;
    public InternalErrorException (int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
