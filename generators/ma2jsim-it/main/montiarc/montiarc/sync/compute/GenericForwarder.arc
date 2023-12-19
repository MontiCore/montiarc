/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

component GenericForwarder<T> {
  port
   in T i,
   out T o;

  <<sync>> compute {
    o = i;
  }
}
