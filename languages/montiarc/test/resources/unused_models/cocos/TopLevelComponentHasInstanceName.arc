/* (c) https://github.com/MontiCore/monticore */
package components;


/**
 * Invalid model.
 * The top-level component must not have a instance name.
 *
 * @implements [Hab16] B2: Top-level component type definitions do not have
                            instance names. (p. 59. Lst. 3.32)
 */
component TopLevelComponentHasInstanceName instance {

  component Sender innerComponent{
    
  }

}
