/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

component Branch1 {
  port in boolean i1, i2;
  port out boolean o1, o2;

  Leaf n1;
  Branch2 n2;

  i1 -> n1.i1;
  i2 -> n1.i2;
  n1.o1 -> n2.i1;
  n1.o2 -> n2.i2;
  n2.o1 -> o1;
  n2.o2 -> o2;

}
