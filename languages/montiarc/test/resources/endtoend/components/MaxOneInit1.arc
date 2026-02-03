/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: Component declares two init blocks (only one init is allowed).
 */
component MaxOneInit1 {

  compute { }

  init { int x = 0; }
  init { }
}
