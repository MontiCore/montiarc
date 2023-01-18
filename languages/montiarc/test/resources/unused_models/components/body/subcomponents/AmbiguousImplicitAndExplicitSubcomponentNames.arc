/* (c) https://github.com/MontiCore/monticore */
package components.body.subcomponents;

/*
 * Invalid model.
 * Identifiers of subcomponents are not unique.
 *
 * Formerly named "E2" in MontiArc3.
 *
 * @implements [Hab16] CV7: Avoid using implicit and explicit names for
 *                            elements with the same type. (p. 72, Lst. 3.54)
 * @implements [Hab16] B1: All names of model elements within a component
                              namespace have to be unique (p.59)
 * TODO CV7 CoCo and Test
 */
component AmbiguousImplicitAndExplicitSubcomponentNames {

  component components.body.subcomponents._subcomponents
              .HasStringInputAndOutput;
      // ERROR: The component 'AmbiguousImplicitAndExplicitSubcomponentNames'
      //          already contains a reference named 'hasStringInputAndOutput'!
      // Warning: Implicit naming should be used for unique subcomponent types only

  component components.body.subcomponents._subcomponents
              .HasStringInputAndOutput;
      // ERROR: The component 'AmbiguousImplicitAndExplicitSubcomponentNames'
      //          already contains a reference named 'hasStringInputAndOutput'!
      // Warning: Implicit naming should be used for unique subcomponent types only

  component components.body.subcomponents._subcomponents
              .HasPortWithImportedType someName;
      // ERROR: The component 'AmbiguousImplicitAndExplicitSubcomponentNames'
      //          already contains a reference named 'someName'!
      // Warning: Implicit naming should be used for unique subcomponent types only

  component components.body.subcomponents._subcomponents
              .AtomicComponent someName;
      // ERROR: The component 'AmbiguousImplicitAndExplicitSubcomponentNames'
      //          already contains a reference named 'someName'!
      // Warning: Implicit naming should be used for unique subcomponent types only

  component components.body.subcomponents._subcomponents
              .HasPortsOfHierarchicalCDTypes hasPortsOfHierarchicalCDTypes;
      // ERROR: The component already contains a reference named
      //          'hasPortsOfHierachicalCDTypes'!

  component components.body.subcomponents._subcomponents.HasPortsOfHierarchicalCDTypes;
      // ERROR: The component 'AmbiguousImplicitAndExplicitSubcomponentNames' already
      //          contains a reference named 'hasPortsOfHierachicalCDTypes'!
      // Warning: Implicit naming should be used for unique subcomponent types only

  component components.body.subcomponents._subcomponents
                .HasGenericInputAndOutputPort<java.lang.String>;

}
