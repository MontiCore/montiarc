/* (c) https://github.com/MontiCore/monticore */
package prepost;

spec Delay<T> {

  port in T x;
  port out T z;
  T buffer;

  ------------------------

  post: buffer = x  &&  z = buffer@pre

}
