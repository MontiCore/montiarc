/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

/**
 * TCP configuration
 */
public class RemoteReceiverConfig {

  private final String address;

  private final int tcpPort;

  public RemoteReceiverConfig(String a, int p) {
    address = a;
    tcpPort = p;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @return the tcpPort
   */
  public int getTcpPort() {
    return tcpPort;
  }
}
