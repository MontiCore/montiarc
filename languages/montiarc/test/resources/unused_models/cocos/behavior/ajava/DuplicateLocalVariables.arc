/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace
 * have to be unique. (p. 59, lst. 3.31)
 */
component DuplicateLocalVariables {
  int i;

  compute {
    int j = 1;

    varif (true) {
      int k = 1;
    }

    varif (true) {
      int i = 1; // Error: variable i already exists
    }

    for (int i=0; i<10; i++) { //Error: variable i already exists

    }

    for (int j=0; j<10; j++) { //Error: variable j already exists
      int i = 2; //Error: variable i already exists
    }
  }

}
