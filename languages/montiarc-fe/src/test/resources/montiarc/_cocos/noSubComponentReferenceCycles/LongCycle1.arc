/* (c) https://github.com/MontiCore/monticore */
package noSubComponentReferenceCycles;

/**
 * Invalid model. Part of cycle LongCycle1 -> LongCycle2 -> LongCycle3 -> longCycle1
 */
component LongCycle1 {
  LongCycle2 lc2;
}
