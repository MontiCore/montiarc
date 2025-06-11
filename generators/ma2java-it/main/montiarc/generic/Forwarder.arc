/* (c) https://github.com/MontiCore/monticore */
package generic;

component Forwarder<T> {
  port
   sync in T input,
   sync out T output;

  compute {
    output = input;
  }
}
