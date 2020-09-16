package calc;

import javafx.util.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class CalcIT {
  public static final String BASE_URI = "http://localhost:8080/calc/";
  private static double serverTimeout = Duration.seconds(10).toMillis();
  private Thread thread;
  private Client client;

  private String getValidBasicAuth() {
    return "Basic " + Base64.getEncoder().encodeToString("franz:opensesame".getBytes());
  }

  private String getInvalidBasicAuth() {
    return "Basic " + Base64.getEncoder().encodeToString("franz:rejectionhurts".getBytes());
  }

  @Before
  public void setUp() {
    thread = new Thread(() -> CalcApplication.main(new String[]{}));
    thread.start();
    // todo block until endpoint is up
    long startTime = System.currentTimeMillis();
    client = ClientBuilder.newClient();
    while (true) {
      try {
        if (client.target(BASE_URI).path("live").request().get().getStatus() == 200) {
          break;
        }
      } catch (Exception ignored) {
      }
      if (System.currentTimeMillis() - startTime > serverTimeout) {
        throw new RuntimeException("timed out starting server");
      }
    }
  }

  @After
  public void tearDown() {
    // This is ok in testing. In production the process will wait for OS Signal
    thread.stop();
  }

  @Test
  public void testDivideByZero() {
    Response response = client.target(BASE_URI)
        .path("divide")
        .queryParam("first", 2)
        .queryParam("second", 0)
        .request()
        .get();

    assertEquals(400, response.getStatus());
  }

  @Test
  public void testAudit() throws Exception {
    // put a function in the history
    assertEquals(new Integer(10),
        client.target(BASE_URI)
            .path("multiply")
            .queryParam("first", 2)
            .queryParam("second", 5)
            .request()
            .get(Integer.class));

    // Should require creds
    assertEquals(401, client.target(BASE_URI).path("audit").request().get().getStatus());

    // Should reject bad creds
    assertEquals(401,
        client.target(BASE_URI)
            .path("audit")
            .request()
            .header(HttpHeaders.AUTHORIZATION, getInvalidBasicAuth())
            .get()
            .getStatus());

    // Should give history with valid creds
    assertEquals("[{\"type\":\"multiply\",\"first\":2,\"second\":5,\"result\":10}]",
        client.target(BASE_URI)
            .path("audit")
            .request()
            .header(HttpHeaders.AUTHORIZATION, getValidBasicAuth())
            .get(String.class));
  }

  @Test
  public void testMultiply() {
    assertEquals(new Integer(10),
        client.target(BASE_URI)
            .path("multiply")
            .queryParam("first", 2)
            .queryParam("second", 5)
            .request()
            .get(Integer.class));
  }
}
