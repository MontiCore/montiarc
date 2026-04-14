/* (c) https://github.com/MontiCore/monticore */
package compute;

/**
 * Invalid model: An input port is referenced inside an init block.
 */
component InputPortInInitialCompute {

  port in int i;

  init {
    int x = i;
  }

  compute { }
}
