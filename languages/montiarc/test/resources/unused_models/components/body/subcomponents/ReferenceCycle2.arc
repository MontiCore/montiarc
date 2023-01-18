/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/*
 * Invalid model.
 *
 * @implements [Hab16] R13: Subcomponent reference cycles are forbidden.
 *  (p. 68, lst. 3.48)
 */
component ReferenceCycle2 {
    port
        in Integer portIn;

    component ReferenceCycle refCycle;

    connect portIn -> refCycle.portIn;
}
