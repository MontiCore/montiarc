/* (c) https://github.com/MontiCore/monticore */
package montiarc.parser;

/*
 * Parsable Model covering variability syntax.
 */
component VariabilitySyntax {
  feature a, b;

  if (a) {
    port in String i;
  }

  if (b) port in String i;
  else {}

  constraint (a==false || a==true && b==false);
}