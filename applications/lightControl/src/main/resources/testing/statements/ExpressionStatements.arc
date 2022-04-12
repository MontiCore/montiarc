/* (c) https://github.com/MontiCore/monticore */
package testing.statements;

/**
 * model for testing the generation of some math-, cast- and function-call-expressions
 */
component ExpressionStatements {

  port in int a;
  port out int b;
  port out double d;

  compute {
    // just do some random calculations
    b = (a - 14) % 10 + ("$O0"+a).indexOf("1") * 2;
    d = (6) + a * 4 - (a + 1.7) / 12;
  }
}