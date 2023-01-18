/* (c) https://github.com/MontiCore/monticore */
package generic;

component Forwarder<T> {
  port
   in T input,
   out T output;

  compute {
    output = input;
  }
}
