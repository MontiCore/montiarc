/* (c) https://github.com/MontiCore/monticore */
package components;

// import AmbiguousModel;
import components.AmbiguousNamedCD.AmbiguousClass;

/*
 * Invalid model.
 * All port and component types have to be unique in the whole system.
 *
 * Formerly named "CG8" in MontiArc3.
 *
 * @implements TODO No literature reference
 */
component AmbiguousPortAndComponentTypes {

  port in AmbiguousModel,
    out AmbiguousClass;

  component AmbiguousModel; //Name clash
  component AmbiguousClass;
}
