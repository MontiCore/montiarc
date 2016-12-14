package robotmodels.io;

/**
 * A conceptual logging component using generic types and configuration 
 * parameters.
 * See: Logger.cmp
 */
component Logger<T>(String prefix){
    port
        in T message;
}