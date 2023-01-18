/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model. Uses Java keywords as identifiers for ports.
 */
component KeywordAsPortName {

  port in char,
   out String goto,
   in Int<int>,
   out Class;
}
