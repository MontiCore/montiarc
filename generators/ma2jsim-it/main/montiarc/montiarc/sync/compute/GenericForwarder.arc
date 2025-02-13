/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.compute;

component GenericForwarder<T> {
  port
   sync in T i,
   sync out T o;

  compute {
    o = i;
  }
}
