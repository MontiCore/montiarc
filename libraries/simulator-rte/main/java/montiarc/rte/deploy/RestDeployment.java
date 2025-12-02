/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.deploy;

import com.google.common.base.Preconditions;
import com.sun.net.httpserver.HttpServer;
import de.se_rwth.commons.logging.Log;
import montiarc.rte.component.Component;
import montiarc.rte.deploy.rest.SimpleRest;
import montiarc.rte.deploy.util.DeSerializer;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public abstract class RestDeployment<T extends Component> implements DeploymentStrategy<T> {

  protected SimpleRest server;
  protected DeSerializer deSerializer;

  @Override
  public void setDeSerializer(DeSerializer deSerializer) {
    this.deSerializer = deSerializer;
  }

  @Override
  public void connect(T component, Map<String, String> options) {
    try {
      this.server = Preconditions.checkNotNull(initRest(options));
    } catch (IOException e) {
      throw new RuntimeException("Failed to start REST server", e);
    }
    setupEndpoints(component);
  }

  protected abstract void setupEndpoints(T component);

  protected SimpleRest initRest(Map<String, String> options) throws IOException {
    String host = options.getOrDefault("serverHost", "127.0.0.1").replace("\"", "");
    int port = 8020;
    try {
      port = Integer.parseInt(options.getOrDefault("serverPort", "8020"));
    } catch (NumberFormatException ignored) { }
    Log.info("Serving on http://" + host + ":" + port, "RestDeployment");
    HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
    server.setExecutor(null);

    return new SimpleRest(server);
  }

  @Override
  public void disconnect() {
    server.stop();
    server = null;
  }

  @Override
  public void addCLIOptions(Options options) {
    options.addOption(Option.builder().longOpt("serverHost")
      .required(false)
      .desc("Sets the rest server host (by default: 127.0.0.1)")
      .hasArg().argName("host")
      .get());
    options.addOption(Option.builder().longOpt("serverPort")
      .required(false)
      .desc("Sets the rest server port (by default: 8020)")
      .hasArg().argName("port")
      .get());
  }
}
