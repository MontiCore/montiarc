/* (c) https://github.com/MontiCore/monticore */
package testing.statements;

/**
 * model for testing the generation of some simple statements
 */
component IfStatements {

  port in int number;
  port out String t;

  compute {
    // variable declaration statement
    int value = number;
    // if-statement
    if (number % 2 == 0){
      value = number + 3;
    }
    // else statement
    if (value <= 20){
      t = "then"+value;
    } else {
      t = "else"+value;
    }
  }
}
