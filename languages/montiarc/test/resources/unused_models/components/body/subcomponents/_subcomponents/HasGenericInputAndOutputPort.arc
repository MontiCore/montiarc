/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents._subcomponents;

/*
 * Valid component.
 * Formerly named "R6GenericPartner<T>" in MontiArc3.
 * Also replaces "arc/context/a/CG3true.arc"
 */
component HasGenericInputAndOutputPort<T> {

  port
    in T tIn,
    out T tOut;

  automaton {
    state Initial;
    initial Initial;
    Initial -> Initial / {tOut = tIn};
  }
}
