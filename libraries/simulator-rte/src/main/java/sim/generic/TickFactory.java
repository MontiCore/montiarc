package sim.generic;


/**
 * Produces and manages ticks.
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 05.12.2008
 * @deprecated use {@link Tick#get()} instead. Will be removed after 
 * 2.5.0 release.
 */
@Deprecated
public final class TickFactory {
    
    /**
   * 
   */
    private TickFactory() {

    }
    
    /**
     * @param <T> type of the tick
     * @param tickType type name
     * @return tick from the given type
     * @deprecated use {@link Tick#get()} instead. Will be removed after 2.5.0 release.
     */
    @Deprecated
    public static <T> Tick<T> getTick(String tickType) {
        Tick<T> t = Tick.get();
        return t;
    }
    
    /**
     * Adds a {@link Tick} of type T to the managed tick map.
     * 
     * @param <T> type of the tick
     * @param type full qualified type as a String
     * @param tick tick to manage
     * @deprecated use {@link Tick#get()} to acquire a tick. Will be removed after 2.5.0 release.
     */
    @Deprecated
    public static <T> void addTick(String type, Tick<T> tick) {
        // do nothing
    }
    
}
