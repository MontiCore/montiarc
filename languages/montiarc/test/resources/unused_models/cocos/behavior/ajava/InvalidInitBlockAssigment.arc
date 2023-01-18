/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model. Cannot assign String to integer variable
 */
component InvalidInitBlockAssigment {

  Integer i;

  init {
    i = "String";
  }

  compute {
  }
}
