/* (c) https://github.com/MontiCore/monticore */
package composition;

/**
 * Invalid model: The subcomponent subA, which is the source and the target of
 * connectors, is missing (the subcomponent cannot be resolved).
 */
component MissingSubcomponentInConnector {

  port in int i;
  port out int o;

  component Inner { }

  Inner subB;

  i -> subA.i;
  subA.o -> o;

}
