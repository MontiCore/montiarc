/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.AbstractComponent;
import montiarc.rte.deploy.rest.SimpleRest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Optional;

public abstract class RestDeployment<T extends AbstractComponent<?, ?>> extends Deployment<T> {

  protected long msPerStep = 1000;

  @Override
  public void deploy(String[] args) {
    Log.ensureInitialization();

    final T component = Objects.requireNonNull(buildComponent());
    SimpleRest server = null;
    try {
      server = Objects.requireNonNull(initRest());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    connect(component, server);

    server.start();
    runSimulation(component);
  }

  @Override
  protected void runSimulation(T component) {
    component.init();
    component.getScheduler().runIndefinitely(component, msPerStep * 1000000);
  }

  protected SimpleRest initRest() throws IOException {
    String host = System.getenv().getOrDefault("REST_SERVER_HOST", "127.0.0.1");
    int port = 8020;
    try {
      port = Integer.parseInt(System.getenv().getOrDefault("REST_SERVER_PORT", "8020"));
    } catch (NumberFormatException ignored) {}
    Log.info("Serving on http://" + host + ":" + port, "RestDeployment");
    HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
    server.setExecutor(null);

    return new SimpleRest(server);
  }

  protected abstract void connect(T component, SimpleRest server);

  public String serialize(Object o) {
    ObjectMapper om = new ObjectMapper();
    try {
      return om.writeValueAsString(o);
    } catch (IOException ignored) {}

    return "";
  }

  public <R> Optional<R> deserialize(String s, Class<R> clazz) {
    ObjectMapper om = new ObjectMapper();

    try {
      return Optional.of(om.readValue(s, clazz));
    } catch (IOException ignored) {}
    return Optional.empty();
  }
}
