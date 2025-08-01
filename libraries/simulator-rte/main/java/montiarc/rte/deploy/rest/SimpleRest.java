/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class SimpleRest {

  protected HttpServer server;

  public SimpleRest(HttpServer server) {
    this.server = server;
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop(0);
  }

  public void subscribe(String path, Function<String, Boolean> action) {
    server.createContext(path, new SimpleHandler(action));
  }

  static class SimpleHandler implements HttpHandler {

    protected Function<String, Boolean> action;

    public SimpleHandler(Function<String, Boolean> action) {
      this.action = action;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
      final boolean res = action.apply(new String(t.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
      if (res) {
        String response = "Done";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
      } else {
        String response = "Error processing request";
        t.sendResponseHeaders(400, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
    }
  }
}
