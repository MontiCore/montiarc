/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/*
 * Invalid model.
 *
 * @implements [Hab16] B1: All names of model elements within a component namespace
 * have to be unique. (p. 59, lst. 3.31)
 */
component JavaPVariableIdentifiersUnique {


  port
    in String s;

  Integer i;

  compute {
    String s = "Hello";
    Integer i;
    int w = 2;
    boolean w = false;
  }

}
