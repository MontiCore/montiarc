/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import sim.generic.TickedMessage;
import sim.port.Port;

import java.util.LinkedList;
import java.util.List;

/**
 * Port that transmits over tcp.
 *
 * @param <T>
 */
public class TCPPort<T> extends Port<T> implements IInTCPPort<T>, IOutTcpPort<T> {

  private boolean isTcpBased = false;

  private final List<OutPortTCPServer<T>> senderServer;

  private final List<InPortTCPServer<T>> inputServer;

  /**
   * Constructor for sim.remote.TCPPort.
   */
  public TCPPort() {
    senderServer = new LinkedList<OutPortTCPServer<T>>();
    inputServer = new LinkedList<InPortTCPServer<T>>();
  }

  /**
   * @see sim.port.Port#accept(sim.generic.TickedMessage)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void accept(TickedMessage<? extends T> message) {
    if (isTcpBased) {
      TickedMessage<T> tickedMessage = (TickedMessage<T>) message;
      send(tickedMessage);
    } else {
      doAccept(message);
    }
  }

  private synchronized void doAccept(TickedMessage<? extends T> message) {
    super.accept(message);
  }

  /**
   * @see sim.port.IPort#send(TickedMessage)
   */
  @Override
  public void send(TickedMessage<T> message) {
    if (isTcpBased) {
      for (OutPortTCPServer<T> sever : senderServer) {
        sever.sendMessage(message);
      }
    } else {
      super.send(message);
    }
  }


  /**
   * @see sim.remote.IOutTcpPort#addReceiver(java.lang.String, int)
   */
  @Override
  public void addReceiver(String address, int tcpPort) {
    isTcpBased = true;
    RemoteReceiverConfig cfg = new RemoteReceiverConfig(address, tcpPort);
    OutPortTCPServer<T> server = new OutPortTCPServer<T>(cfg);
    new Thread(server, this.toString()).start();
    senderServer.add(server);
  }

  /**
   * @see sim.remote.IInTCPPort#startListenOn(int)
   */
  @Override
  public void startListenOn(int tcpPort) {
    InPortTCPServer<T> s = new InPortTCPServer<T>(this, tcpPort);
    new Thread(s, this.toString()).start();
    inputServer.add(s);
  }

  public void stop() {
    for (OutPortTCPServer<T> o : senderServer) {
      o.stop();
    }
    for (InPortTCPServer<T> i : inputServer) {
      i.stop();
    }
  }
}
