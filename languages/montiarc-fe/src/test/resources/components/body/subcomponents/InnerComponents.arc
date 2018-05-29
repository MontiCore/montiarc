package components.body.subcomponents;

/**
 * Valid model.
 *
 * Subcomponent count: 6
 */
component InnerComponents {
  component SimpleComponentWithAutomaton ref;
  component Inner ref1;

  // Instance created
  component Inner {
    component SimpleComponentWithAutomaton ref;

    // TODO: Instance created?
    component InnerInner {
      component SimpleComponentWithAutomaton ref;
    }

    component InnerInner ref1;
  }

  // Automatic instance created
  component InnerWithoutPorts {
    //Empty body
  }

  // Automatic instance created
  component InnerSimpleComponent {

    // Automatic instance created
    component InnerInnerComponent {
      // Empty body
    }

    // No instance created
    component InnerInnerGenericComponent<K, V> {
      // Empty body
    }

    // No instance created
    component InnerInnerConfigurableComponent(int x, String z, U v) {
      // Empty body
    }

    // No instance created
    component InnerInnerGenericAndConfigurableComponent<K, V>(String s, int i) {
      // Empty body
    }

  }

  // No instance created
  component InnerGenericComponent<K, V> {
    // Empty body
  }

  // No instance created
  component InnerConfigurableComponent(int x, String z, U v) {
    // Empty body
  }

  // No instance created
  component InnerGenericAndConfigurableComponent<K, V>(String s, int i) {
    // Empty body
  }

  // Explicit instance creation
  component BlaBla myBla {
    // Empty body
  }
}