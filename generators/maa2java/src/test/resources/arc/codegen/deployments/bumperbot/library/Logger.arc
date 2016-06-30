package bumperbot.library;

/**
 * An abstract logging component using generic types and configuration 
 * parameters.
 * See: Logger.cmp
 */
//abstract 
component Logger<T>(String prefix){
    port
        in T message;
}