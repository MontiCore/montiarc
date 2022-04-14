/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import org.slf4j.Logger;
import sim.generic.TickedMessage;
import sim.port.IInSimPort;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runnable that listens for incoming remote messages on incoming ports.
 *
 * @param <T> communication data type.
 */
class InPortTCPServer<T> implements Runnable {

  private ObjectInputStream in;
  private final int tcpPort;
  private final IInSimPort<T> incomingPort;
  private State state;
  private Socket client;
  private ServerSocket server;
  private boolean running;
  private final Logger logger;

  public InPortTCPServer(final IInSimPort<T> inPort, final int tcpPort) {
    this.incomingPort = inPort;
    this.tcpPort = tcpPort;
    this.running = true;
    this.logger = inPort.getComponent().getErrorHandler().getLogger();
  }

  @Override
  public void run() {
    state = State.DISCONNECTED;
    while (running) {
      switch (state) {
        case DISCONNECTED:
          connect();
          break;
        case CONNECTED:
          handleMessage();
          break;
        case ERROR:
          stop();
          break;
      }
    }
  }


  @SuppressWarnings("unchecked")
  private void handleMessage() {
    TickedMessage<T> message = null;
    try {
      message = (TickedMessage<T>) in.readObject();
    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
      state = State.DISCONNECTED;
      try {
        in.close();
        client.close();
        server.close();
      } catch (IOException e1) {
        logger.debug(e1.getMessage(), e1);
      }
    } catch (ClassNotFoundException e) {
      state = State.ERROR;
      logger.debug(e.getMessage(), e);
    }
    if (message != null) {
      incomingPort.accept(message);
    }
  }

  private void connect() {
    server = null;
    client = null;
    in = null;
    try {
      server = new ServerSocket(tcpPort);
      server.setSoTimeout(1000);
      client = server.accept();
      in = new ObjectInputStream(client.getInputStream());
      state = State.CONNECTED;
    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
      try {
        if (server != null) {
          server.close();
        }
      } catch (IOException e1) {
        logger.debug(e1.getMessage(), e1);
      }
      state = State.DISCONNECTED;
    }
  }

  public void stop() {
    this.running = false;
  }
}

