/* (c) https://github.com/MontiCore/monticore */
package components.body.timing.delays;

/**
 * Valid model.
 *
 * @brief adds a fix delay to a channel.
 * 
 * All messages received on port {@link portIn} are relayed
 * to {@link portOut} after a fix delay of {@code delay} time
 * units.
 * 
 *
 *
 * @type T type of the delayed channel.
 * @param delay constant delay in time units.
 */
component ConstantDelay<T>(int delay) {
  
  timing delayed;
  
  port 
    in T portIn,
    out T portOut;
   
}
