/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents._subcomponents;

/*
 * Valid component.
 *
 * Formerly named "x.CG2Partner<T>" in MontiArc3.
 */
component HasThreeGenericInAndOneOutPort<T> {

  port
    in T tIn1,
    in T tIn2,
    in T tIn3,
    out T tOut;
}
