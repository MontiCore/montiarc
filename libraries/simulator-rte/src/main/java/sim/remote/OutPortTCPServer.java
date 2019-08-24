/* (c) https://github.com/MontiCore/monticore */
package sim.remote;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.generic.TickedMessage;

/**
 * TODO: Write me!
 * 
 * @author (last commit) $Author: ahaber $
 * @version $Revision: 2964 $, $Date: 2014-08-25 18:37:47 +0200 (Mo, 25 Aug 2014) $
 * @since 2.5.0
 */
class OutPortTCPServer<T> implements Runnable {
    private boolean running = true;
    
    private final Queue<TickedMessage<T>> unsendMsgs;
    
    private final RemoteReceiverConfig cfg;
    
    private Socket socket;
    
    private ObjectOutputStream stream;
    
    private final Logger logger;
    
    /**
     * Constructor for sim.remote.OutPortTCPServer.
     * 
     * @param cfg used {@link RemoteReceiverConfig}.
     * @param logger logger to use.
     */
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
            }
            else {
                processMessages();
            }
        }
    }
    
    /**
     * TODO: Write me!
     */
    private void processMessages() {
        while (!unsendMsgs.isEmpty()) {
            TickedMessage<T> msg = unsendMsgs.poll();
            try {
                stream.writeObject(msg);
            }
            catch (IOException e) {
                logger.debug(e.getMessage(), e);
                try {
                    unsendMsgs.add(msg);
                    socket.close();
                    stream.close();
                    break;
                }
                catch (IOException e1) {
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
        }
        catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }
    }
    
    public void stop() {
        running = false;
    }
    
}
