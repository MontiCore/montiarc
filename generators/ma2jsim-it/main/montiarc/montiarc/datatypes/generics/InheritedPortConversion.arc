/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

component InheritedPortConversion extends IntermediateIn<char> {

  In<int> c2i;
  In<long> c2l;

  p -> c2i.p;
  p -> c2l.p;
}
