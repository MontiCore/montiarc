/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: Component declares an init block twice (only one init is allowed).
 */
component MaxOneInit3 {

  compute { }

  init { int y = 2; }
  init { }
}
