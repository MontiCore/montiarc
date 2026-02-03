/* (c) https://github.com/MontiCore/monticore */
package components;

/**
 * Invalid model: Inner component declares two init blocks (only one init is allowed).
 */
component MaxOneInit2 {

  compute { }

  component Inner {

    compute { }

    init { int y = 2; }
    init { }
  }

  init { }
}
