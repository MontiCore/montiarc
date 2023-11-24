/* (c) https://github.com/MontiCore/monticore */
package dataTypes;

/**
 *  Simple atomic component for testing booleans
 */
component BooleanComponent {
  port in Boolean in;
  port out Boolean out;

  <<sync>> automaton{
    initial state Idle;

    Idle -> Idle /{
      out = in || false;
    };
  }
}
