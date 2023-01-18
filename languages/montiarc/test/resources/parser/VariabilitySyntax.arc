/* (c) https://github.com/MontiCore/monticore */
package parser;

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

  component A {
    feature c;
  }

  A a1(c=true);

  constraint (a==false || a==true && b==false);
}
