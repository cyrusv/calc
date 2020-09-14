package calc;

import javafx.util.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class CalcIT {
  public static final String BASE_URI = "http://localhost:8080/calc/";
  private Thread thread;
  private static double serverTimeout = Duration.seconds(10).toMillis();
  private Client client;

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
  public void testMain() {
    assertEquals(new Integer(10), client.target(BASE_URI).path("multiply")
        .queryParam("first", 2)
        .queryParam("second", 5)
        .request().get(Integer.class));

    assertEquals("[{\"type\":\"multiply\",\"first\":2,\"second\":5,\"result\":10}]",
        client.target(BASE_URI)
            .path("history")
            .request()
            .get(String.class));
  }
}
