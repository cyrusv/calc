package calc;

import io.confluent.rest.Application;
import io.confluent.rest.RestConfigException;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Configurable;
import java.util.HashMap;

public class CalcApplication extends Application<CalcConfig> {
  private static final Logger log = LoggerFactory.getLogger(CalcApplication.class);

  public CalcApplication(CalcConfig config) {
    super(config);
  }

  public static void main(String[] args) {
    try {
      HashMap<String, String> props = new HashMap<>();
      props.put("authentication.method", "BASIC");
      CalcApplication app = new CalcApplication(new CalcConfig(props));
      app.start();
      log.info("Server started, listening for requests on {}...", app.server.getURI());
      app.join();
    } catch (RestConfigException e) {
      log.error("Server configuration failed: " + e.getMessage());
      System.exit(1);
    } catch (Exception e) {
      log.error("Server died unexpectedly: " + e.toString());
      throw new RuntimeException(e);
    }
  }

  public void setupResources(Configurable<?> configurable, CalcConfig calcConfig) {
    configurable.register(new CalcResource());
  }

  @Override
  protected ConstraintMapping createGlobalAuthConstraint() {
    final Constraint constraint = new Constraint();
    constraint.setAuthenticate(true);
    constraint.setRoles(new String[]{"thinker"});

    final ConstraintMapping mapping = new ConstraintMapping();
    mapping.setConstraint(constraint);
    mapping.setMethod("*");
    mapping.setPathSpec("/calc/audit");
    return mapping;
  }

  @Override
  protected LoginService createLoginService() {
    return new ToyLoginService();
  }

  @Override
  protected IdentityService createIdentityService() {
    return null;
  }

  private static class ToyLoginService extends AbstractLoginService {

    @Override
    protected String[] loadRoleInfo(UserPrincipal user) {
      if (user.getName().equals("franz")) {
        return new String[]{"thinker"};
      }
      return new String[0];
    }

    @Override
    protected UserPrincipal loadUserInfo(String username) {
      if (username.equals("franz")) {
        return new UserPrincipal(username, new Password("opensesame"));
      }
      return null;
    }
  }

}
