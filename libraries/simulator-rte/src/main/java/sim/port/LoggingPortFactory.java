/* (c) https://github.com/MontiCore/monticore */
/**
 * 
 */
package sim.port;

import org.slf4j.Logger;


/**
 * {@link IPortFactory} for {@link LoggingPort}s.
 *
 *
 */
public class LoggingPortFactory implements IPortFactory {
    
    /**
     * Logger to use.
     */
    private final Logger logger;
    
    private final boolean doLogTicks;
    
    /**
     * 
     * @param logger logger to use
     */
    public LoggingPortFactory(final Logger logger) {
        this(logger, true);
    }
    
    /**
     * 
     * @param logger logger to use
     * @param doLogTicks flags, if ticks shall be logged, too.
     */
    public LoggingPortFactory(final Logger logger, final boolean doLogTicks) {
        this.logger = logger;
        this.doLogTicks = doLogTicks;
    }
    
    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createInPort()
     */
    @Override
    public <T> IInSimPort<T> createInPort() {
        return new LoggingPort<T>(logger, doLogTicks);
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createOutPort()
     */
    @Override
    public <T> IOutSimPort<T> createOutPort() {
        return new LoggingPort<T>(logger, doLogTicks);
    }

    /* (non-Javadoc)
     * @see sim.port.IPortFactory#createForwardPort()
     */
    @Override
    public <T> IForwardPort<T> createForwardPort() {
        return new ForwardPort<T>();
    }
}
