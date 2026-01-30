/* (c) https://github.com/MontiCore/monticore */
package composition;

/*
 * Invalid model: The atomic component contains an autoconnect statement.
 */
component AutoconnectInAtomic4 {

  automaton { initial state S; }

  autoconnect port;

}
