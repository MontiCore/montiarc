/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.primitive.composition;

// Should compile, by converting the values of a port that is inherited from a
// super component to the types of the ports it is connected to
component InheritedPortConversion extends CharPort {

  IntIn c2i;
  LongIn c2l;

  p -> c2i.p;
  p -> c2l.p;
}
