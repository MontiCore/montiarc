/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sim.generic.TickedMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;

class OutPortTCPServer<T> implements Runnable {
  private boolean running = true;

  private final Queue<TickedMessage<T>> unsendMsgs;

  private final RemoteReceiverConfig cfg;

  private Socket socket;

  private ObjectOutputStream stream;

  private final Logger logger;

  protected OutPortTCPServer(RemoteReceiverConfig cfg) {
    unsendMsgs = new LinkedList<TickedMessage<T>>();
    this.cfg = cfg;
    this.logger = LoggerFactory.getLogger(getClass());
  }

  protected synchronized void sendMessage(TickedMessage<T> msg) {
    unsendMsgs.add(msg);
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    while (running) {
      if (socket == null || socket.isClosed() || !socket.isConnected()) {
        connect();
      } else {
        processMessages();
      }
    }
  }

  private void processMessages() {
    while (!unsendMsgs.isEmpty()) {
      TickedMessage<T> msg = unsendMsgs.poll();
      try {
        stream.writeObject(msg);
      } catch (IOException e) {
        logger.debug(e.getMessage(), e);
        try {
          unsendMsgs.add(msg);
          socket.close();
          stream.close();
          break;
        } catch (IOException e1) {
          logger.debug(e1.getMessage(), e1);
          break;
        }
      }
    }
  }

  private void connect() {

    SocketAddress a = new InetSocketAddress(cfg.getAddress(), cfg.getTcpPort());
    socket = new Socket();
    try {
      socket.connect(a, 1000);
      stream = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
    }
  }

  public void stop() {
    running = false;
  }
}
