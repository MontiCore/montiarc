/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/*
 * Invalid model. Component names have to start with a capital letter
 *
 * @implements [Hab16] CV2: Types start with an upper-case letter. (p.71, no listing)
 */
component InnerViolatesComponentNaming {

    port
        in String s1;
    component violates { // Component name does not start with a capital letter
        port
            in String s2;
    }

    component violates v; // Component name does not start with a capital letter
    connect s1 -> v.s2;
}
