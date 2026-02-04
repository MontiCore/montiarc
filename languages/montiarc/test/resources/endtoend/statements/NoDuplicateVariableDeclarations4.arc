/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
  * Invalid model: Variable 'i' is declared multiple times in the
  * same compute-block (scope).
  */
component NoDuplicateVariableDeclarations4 {
  compute {
    int i = 1;
    int i = 1;
  }
}
