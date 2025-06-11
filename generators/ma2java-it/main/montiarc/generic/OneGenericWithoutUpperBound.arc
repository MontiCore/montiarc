/* (c) https://github.com/MontiCore/monticore */
package generic;

component OneGenericWithoutUpperBound<T> {
  port
   sync in T i,
   sync out T o;

  compute {
      o = i;
  }
}
