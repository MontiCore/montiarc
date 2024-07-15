/* (c) https://github.com/MontiCore/monticore */

package mceffect.checker;

component Main {

  port in boolean i1,
       in boolean i2,
       out boolean o1,
       out boolean o2;

  A a;
  B b;

  i1 -> a.i1;
  i2 -> a.i2;

  a.o1 -> b.i1;
  a.o2 -> b.i2;

  b.o1 -> o1;
  b.o2 -> o2;


}