/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model.
 * Cannot change values on incoming ports.
 */
component ChangeIncomingPortInCompute {

  port
    in Integer incomingInt,
    out Integer outgoingInt;

  compute {
    incomingInt = 10;             // Error: incoming port was changed
    outgoingInt = incomingInt;
    incomingInt += 10;            // Error
    outgoingInt += 10;
  }
}
