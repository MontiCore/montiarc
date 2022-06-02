/* (c) https://github.com/MontiCore/monticore */
import in.InComp;
import out.OutComp;

/*
 * Valid model.
 */
component Composed {
  InComp ic;
  OutComp oc;

  oc.outPort -> ic.inPort;
}