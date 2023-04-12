/* (c) https://github.com/MontiCore/monticore */
package parser;

/*
 * Parsable Model covering variability syntax.
 */
component VariabilitySyntax {
  feature a, b;

  varif (a) {
    port in String i;
  }

  varif (b) port in String i;
  else {}

  component A {
    feature c;
  }

  A a1(c=true);

  constraint (a==false || a==true && b==false);
}
