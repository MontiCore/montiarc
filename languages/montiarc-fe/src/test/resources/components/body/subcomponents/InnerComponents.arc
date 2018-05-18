package components.body.subcomponents;

/**
 * Valid model. 
 */
component InnerComponents {
  component SimpleComponentWithAutomaton ref;
  component Inner ref1;

  component Inner {
    component SimpleComponentWithAutomaton ref;

    component InnerInner {
      component SimpleComponentWithAutomaton ref;
    }

    component InnerInner ref1;
  }

  component InnerWithoutPorts {
    //Empty body
  }

  component InnerSimpleComponent {

    component InnerInnerComponent {
      // Empty body
    }

    component InnerInnerGenericComponent<K, V> {
      // Empty body
    }

    component InnerInnerConfigurableComponent(int x, String z, U v) {
      // Empty body
    }

    component InnerInnerGenericAndConfigurableComponent<K, V>(String s, int i) {
      // Empty body
    }

  }

  component InnerGenericComponent<K, V> {
    // Empty body
  }

  component InnerConfigurableComponent(int x, String z, U v) {
    // Empty body
  }

  component InnerGenericAndConfigurableComponent<K, V>(String s, int i) {
    // Empty body
  }

  component BlaBla myBla {
    // Empty body
  }
}