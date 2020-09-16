package calc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic calc functions for integers
 */
@Path("/calc")
@Produces(MediaType.APPLICATION_JSON)
public class CalcResource {
  private final List<MathProblem> history;

  public CalcResource() {
    this.history = new ArrayList<>();
  }

  @GET
  @Path("live")
  public boolean live() {
    return true;
  }

  @GET
  @Path("add")
  public int add(@QueryParam("first") int first, @QueryParam("second") int second) {
    int result = first + second;
    history.add(new MathProblem("add", first, second, result));
    return result;
  }

  @GET
  @Path("multiply")
  public int multiply(@QueryParam("first") int first, @QueryParam("second") int second) {
    int result = first * second;
    history.add(new MathProblem("multiply", first, second, result));
    return result;
  }

  @GET
  @Path("divide")
  public int divide(@QueryParam("first") int first, @QueryParam("second") int second) {
    if (second == 0) {
      throw new WebApplicationException("Can't divide by zero", null, 400);
    }
    int result = first / second;
    history.add(new MathProblem("divide", first, second, result));
    return result;
  }

  @GET
  @Path("audit")
  public List<MathProblem> audit() {
    return history;
  }
}
