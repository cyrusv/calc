package calc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.rest.Application;
import io.confluent.rest.RestConfigException;

import javax.ws.rs.core.Configurable;

public class CalcApplication extends Application<CalcConfig> {
  private static final Logger log = LoggerFactory.getLogger(CalcApplication.class);

  public CalcApplication(CalcConfig config) {
    super(config);
  }

  public void setupResources(Configurable<?> configurable, CalcConfig calcConfig) {
    configurable.register(new CalcResource());
  }

  public static void main(String[] args) {
    try {
      CalcApplication app = new CalcApplication(new CalcConfig());
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

}
