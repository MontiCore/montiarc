/* (c) https://github.com/MontiCore/monticore */
package compute;

/**
 * Invalid model: A non-synchronous input port is referenced inside a compute block.
 */
component NonSyncInputPortInCompute {
  port in int i;

  compute {
    int x = i;
  }
}
