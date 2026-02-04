/* (c) https://github.com/MontiCore/monticore */
package statements;

/**
  * Invalid model: Variable 'i' is declared multiple times in the
  * same init-block (scope).
  */
component NoDuplicateVariableDeclarations5 {
  init {
    int i = 1;
    int i = 1;
  }

  compute { }
}
