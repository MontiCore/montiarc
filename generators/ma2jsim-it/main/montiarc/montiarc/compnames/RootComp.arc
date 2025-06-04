/* (c) https://github.com/MontiCore/monticore */
package montiarc.compnames;

component RootComp {
  port sync in boolean i;

  Leaf directLeaf;
  WithModes withModes;

  i -> withModes.i;
}
