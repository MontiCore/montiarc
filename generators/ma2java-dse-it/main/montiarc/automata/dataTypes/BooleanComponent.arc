/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing booleans
 */
component BooleanComponent {
  port <<sync>> in Boolean in;
  port <<sync>> out Boolean out;

  automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in || false;
    };
  }
}
