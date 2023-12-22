/* (c) https://github.com/MontiCore/monticore */
package generic;

component OneGenericWithoutUpperBound<T> {
  port
   in T i,
   out T o;

  compute {
      o = i;
  }
}
