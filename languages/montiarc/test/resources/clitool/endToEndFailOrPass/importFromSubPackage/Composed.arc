/* (c) https://github.com/MontiCore/monticore */
import importFromSubPackage.in.InComp;
import importFromSubPackage.out.OutComp;

/*
 * Valid model.
 */
component Composed {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}