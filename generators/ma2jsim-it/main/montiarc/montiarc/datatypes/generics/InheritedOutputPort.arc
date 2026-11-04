/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

component InheritedOutputPort extends IntermediateOut<char> {

  Out<char> source;

  source.p -> p;
}
