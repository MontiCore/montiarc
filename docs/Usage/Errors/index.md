---
icon: fontawesome/solid/circle-exclamation
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Errors

When using the tooling, there are multiple errors you might encounter:

### 0xC1100 - *Circular Inheritance*

##### Description
Occurs when a component directly or indirectly inherits from itself, creating an inheritance cycle.

##### Example

```montiarc
component A extends B { }
component B extends A { }
```

##### How to fix
Break the inheritance cycle by refactoring one of the components so that no cyclic dependency remains.

---

### 0xC1101 - *Missing Component*

##### Description
The specified component cannot be found or resolved in the current context.

##### Example

```montiarc
component X extends UnknownComp { }
```

##### How to fix
Ensure that `UnknownComp` exists and is imported or defined in the same package or module.

---

### 0xC1102 - *Ambiguous Reference*

##### Description
More than one component or element matches the given name, making resolution ambiguous.

##### Example

```montiarc
component pkg1.Foo { }
component pkg2.Foo { }
component Bar extends Foo { }
```

##### How to fix
Qualify your reference with the full package path or rename one of the conflicting components.

---

### 0xC1103 - *In Port Unused*

##### Description
An incoming port is declared but never connected or used in the model.

##### Example

```montiarc
component A {
  port in String msg;
  // msg is never  connected
}
```

##### How to fix
Add a connector to route data into this port (e.g., `B.out -> msg;`) or remove the `msg` port if it isn’t required.

---

### 0xC1104 - *Out Port Unused*

##### Description
An outgoing port is declared but never connected or used in the model.

##### Example

```montiarc
component A {
  port out Integer data;
  // data is never sent or connected
}
```
##### How to fix
Connect this port to a target (e.g., `data -> B.in;`) or remove the `data` port if it isn’t delivering relevant output.
---

### 0xC1105 - *Port Multiple Sender*

##### Description
An incoming port receives values from more than one connector, causing conflicting data sources.

##### Example

```montiarc
component A {
  port in Integer x;
  port out Integer a, b;
  a -> x;
  b -> x; // second sender to x
}
```
##### How to fix
Ensure each input port has at most one source.

---

### 0xC1106 - *In Port Not Connected*

##### Description
An incoming port is declared but left unconnected, so it never receives data.

##### Example

```montiarc
component A {
  port in Integer inVal;
  // missing connector for inVal
}
```
##### How to fix
Create a connector to supply data to this port, for example:

```montiarc
component A {
  port in Integer inVal;
  component Source src; 
  // src has an out port o
  B.o -> inVal;
}
```

Or remove `inVal` if it’s not needed.

---

### 0xC1107 - *Out Port Not Connected*

##### Description
An outgoing port is declared but has no connectors, so its output is never delivered.

##### Example

```montiarc
component A {
  port out Integer outVal;
  // missing connector from outVal
}
```

---

### 0xC1108 - *Missing Port*

##### Description
A referenced port does not exist on the specified component.

##### Example

```montiarc
component A {
  port out String o;
  o -> unknown;
}
```

---

### 0xC1109 - *Missing Subcomponent*

##### Description
A referenced subcomponent cannot be found in the component definition.

##### Example

```montiarc
component A {
  port in String i;
  // forgot to declare sub
  sub.o -> i;
}
```

---

### 0xC1110 - *Connector Type Mismatch*

##### Description
The type of the source port does not match the target port type.

##### Example

```montiarc
component A {
  port out String txt;
  port in Integer num;
  txt -> num;
}
```

---

### 0xC1111 - *Source Direction Mismatch*

##### Description  
Attempt to connect from an input port as if it were an output. In MontiArc, every connector must go from an output port to an input port.

##### Example

```montiarc
component A {
  port in Integer i1, i2;
  port out Integer o;
  // wrong direction:
  i1 -> i2;
}
```

---

### 0xC1112 - *Target Direction Mismatch*

##### Description  
Attempt to write to an output port as if it were an input. In MontiArc, every connector must go from an output port to an input port.

##### Example

```montiarc
component A {
  port out Integer o1, o2;
  port in Integer i1;
  // wrong directions:
  o1 -> o2 ;
}
```

---

### 0xC1113 - *Connector Timing Mismatch*

##### Description
The timing annotations (`sync`, `delay`, etc.) on source and target ports differ incompatibly.

##### Example

```montiarc
component A {
  port sync out Integer a;
  port delay in Integer b;
  a -> b;
}
```

---

### 0xC1114 - *Multiple Timing Annotations*

##### Description
A port has been annotated with more than one timing modifier.

##### Example

```montiarc
component A {
  port sync delay out Integer x;
}
```

---

### 0xC1115 - *Multiple Behavior*

##### Description
A component defines more than one behavior block.

##### Example

```montiarc
component A {
  automaton { /*...*/ }
  compute { /*...*/ }
}

component B {
  automaton { /*...*/ }
  automaton { /*...*/ }
}
```

---

### 0xC1117 - *Feedback Causality*

##### Description  
Occurs when a model contains a feedback loop without any form of delay, causing signals to propagate instantaneously in a cycle. This leads to undefined or nondeterministic behavior because there is no defined ordering for updates.
Read more about causality and feedback loops [here](../../Reference/Component/Connectors.md#feedback).

##### Example

```montiarc
component A { 
  port in Integer i; 
  port out Integer o;
  
  automaton {
    initial state S;
    S -> S / { o = i; };
  }
}

component B {
  A sub;
  sub.o -> sub.i;
}
// Here, sub.o drives sub.i within the same tick, forming a zero-delay feedback loop.
```

##### How to fix
Add a delay anywhere in the loop: annotate the behavior (e.g., automaton or compute block) with `<<delayed>>` to enforce at least one clock tick between signal propagations for this component:
```montiarc
component A { 
  port in Integer i; 
  port out Integer o;

  <<delayed>> automaton { 
    initial state S;
    S -> S / { o = i; };
  }
}

component B {
  A sub;
  sub.o -> sub.i;
}
```

---

### 0xC1119 - *Too Few Arguments*

##### Description
A component instance is invoked with fewer arguments than required.

##### Example

```montiarc
component A(String s, Integer n) { }
component B {
  A a("hello"); // missing second arg
}
```

##### How to fix  
Provide all required arguments: `A a("hello", 5);`.

---

### 0xC1120 - *Too Many Arguments*

##### Description
A component instance is given more arguments than its signature defines.

##### Example

```montiarc
component A(String s) { }
component B {
  A a("x", 1); // extra arg
}
```

##### How to fix
Remove the extra arguments or adjust the component signature accordingly.

---

### 0xC1122 - *Comp Arg Multiple Values*

##### Description
The same named argument is assigned multiple values in a component instantiation.

##### Example

```montiarc
component A(x=1, x=2) { }
```

---

### 0xC1123 - *Comp Arg Type Mismatch*

##### Description
The provided argument type does not match the expected parameter type.

##### Example

```montiarc
component A(Integer x) { }
component B { A a("string"); }
// an Integer is intended not a String value
```

---

### 0xC1124 - *Comp Arg Key Invalid*

##### Description
An unknown key argument is used when instantiating a component.

##### Example

```montiarc
component A(x=1) { }
component B { A a(y=2); }
```

---

### 0xC1125 - *Comp Arg Value After Key*

##### Description
A positional argument follows a named argument, which is disallowed.

##### Example

```montiarc
component A(x, y) { }
component B { A a(x=1, 2); }
```

##### How to fix
Place all positional arguments before any named arguments.

---

### 0xC1126 - *Optional Params Last*

##### Description  
Optional configuration parameters must come after all mandatory parameters in a component or invocation. Placing an optional parameter before a required one leads to ambiguity and is disallowed.

##### Example

```montiarc
component Comp(int opt = 1, int mand) { } // error: optional 'opt' before mandatory 'mand'
```

##### How to fix
Reorder the parameter list so that all mandatory parameters precede any optional ones:
```montiarc
component Comp(int mand, int opt = 1) { }
```

---

### 0xC1127 - *Subcomponent Reference Cycle*

##### Description  
Subcomponents reference each other circularly.

##### Example

```montiarc
component A { component B b; }
component B { component A a; }
```

##### How to fix
Refactor the model to remove cyclic subcomponent definitions.

---

### 0xC1131 - *Expected an Expression*

##### Description
An expression was expected in a place where it was omitted or malformed.

##### Example

```montiarc
component A { 
  port out Integer x;
  connect x -> ; // missing right-hand expression
}
```

---

### 0xC1132 - *Heritage In Port Type Mismatch*

##### Description
An inherited component redefines an incoming port with a type incompatible with its supertype.

##### Example

```montiarc
component Base { port in String m; }
component Sub extends Base { port in Integer m; }
```

##### How to fix
Align port types with the super component or rename the port.

---

### 0xC1133 - *Heritage Out Port Type Mismatch*

##### Description
An inherited component redefines an outgoing port with an incompatible type.

##### Example

```montiarc
component Base { port out Integer n; }
component Sub extends Base { port out String n; }
```

##### How to fix
Keep the same type as in the abstraction or choose a new port name.

---

### 0xC1134 - *Heritage Port Direction Mismatch*

##### Description
An inherited component changes the direction of a port from its supertype.

##### Example

```montiarc
component Base { port in Integer x; }
component Sub extends Base { port out Integer x; }
```

##### How to fix
Maintain the original direction (in or out) or rename the port.

---

### 0xC1135 - *Port Ref In Static Context*

##### Description
Port references are disallowed in static contexts such as field initializers or varif conditions—because ports only exist at runtime and cannot be accessed during static initialization.

##### Example

```montiarc
component B {
  port in int i;
  varif(i > 0) { } // error: 'i' referenced in static context
}
```

##### How to fix
Remove static usage or pass port values at runtime.

---

### 0xC1137 - *Field Init Type Mismatch*

##### Description
A component field is initialized with a value of the wrong type.

##### Example

```montiarc
component A {
  boolean f = 1; }
```

---

### 0xC1139 - *Param Default Type Mismatch*

##### Description
A parameter’s default value does not match its declared type.

##### Example

```montiarc
component A(boolean p = 1) { }
```

---

### 0xC1142 - *Comp Arg Multi Assignment*

##### Description
Occurs when a component instantiation uses either the wrong assignment operator or assigns the same parameter more than once.

##### Example

```montiarc
component A(x := 5) { }            // wrong operator (must be “=”)
component B(y = 1, y = 2) { }      // duplicate assignment of “y”
```

---

### 0xC1143 - *Component Lower Case*

##### Description
Component names should start with an uppercase letter by convention.

##### Example

```montiarc
component myComp { }
```

---

### 0xC1144 - *Subcomponent Upper Case*

##### Description
Subcomponent instances should start with a lowercase letter.

##### Example

```montiarc
component A { component B Sub; }
```

##### How to fix
Rename to `sub`.

---

### 0xC1145 - *Port Upper Case*

##### Description
Ports should start with a lowercase letter.

##### Example

```montiarc
component A { port in Integer Data; }
```

---

### 0xC1146 - *Parameter Upper Case*

##### Description
Parameters should start with a lowercase letter.

##### Example

```montiarc
component A(String Par) {}
```

---

### 0xC1147 - *Field Upper Case*

##### Description
Component fields should start with a lowercase letter.

##### Example

```montiarc
component A { boolean Bool; }
```

---

### 0xC1148 - *Unique Identifier Names*

##### Description
Multiple identifiers in the same scope share the same name.

##### Example

```montiarc
component A {
  Integer x; String x;
}
```

---

### 0xC1149 - *Restricted Identifier*

##### Description
A reserved or restricted identifier is used where disallowed.

##### Example

```montiarc
component Comp { 
  port in int key;                 // ❌ “key” is restricted
}
```

---

### 0xC1150 - *Unsupported Model Element*

##### Description
A language feature is used that MontiArc does not support and is thus ignored.

##### Example

```montiarc
component A { 
  automaton { 
  final state s; // The final state is unsupported
  } 
}
```

---

### 0xC1151 - *Read From Outgoing Port*

##### Description
Attempt to read data from an outgoing port, which is write-only.

##### Example

```montiarc
component A { port out int o; int x = o; }
```

##### How to fix
Reverse connector direction or use an incoming port for reading.

---

### 0xC1152 - *Write To Incoming Port*

##### Description
Attempt to write data to an incoming port, which is read-only.

##### Example

```montiarc
component A { port in int i; i = 5; }
```

##### How to fix
Use an outgoing port or connector to send data.

---

### 0xC1153 - *Write To Readonly Variable*

##### Description
A component field declared as read-only is being assigned.

##### Example

```montiarc
component A { final int x; x = 1; }
```

---

### 0xC1154 - *Invalid Context Assignment*

##### Description
Assignments are not allowed in this syntactic context. You cannot use an assignment expression (=, +=, -=, etc.) in places where MontiArc does not expect it namely inside subcomponent argument lists or in connector declarations. Such contexts only allow simple parameter bindings or connector arrows, not arbitrary assignments.

##### Example

```montiarc
Component A {
  port out int o;
  port in int i;
  B b(p = p = 1); // Nested assignment in a subcomponent instantiation
  o = i;     // Using '=' instead of '->'
}
```

---

### 0xC1155 - *Invalid Context Increment Prefix*

##### Description
Prefix increment (`++x`) is used in a context where disallowed.

##### Example

```montiarc
component A { B b(p = ++p); }
```

##### How to fix
Perform increments within allowed behavioral code blocks.

---

### 0xC1156 - *Invalid Context Decrement Prefix*

##### Description
Prefix decrement (`--x`) is used where not permitted.

##### Example

```montiarc
component A { B b(p = --p); }
```

##### How to fix
Use within valid behavioral blocks.

---

### 0xC1157 - *Invalid Context Increment Suffix*

##### Description
Suffix increment (`x++`) is disallowed in this context.

##### Example

```montiarc
component A { B b(p = p++); }
```

##### How to fix
Place increments in behavior sections.

---

### 0xC1158 - *Invalid Context Decrement Suffix*

##### Description
Suffix decrement (`x--`) is disallowed here.

##### Example

```montiarc
component A { B b(p = p--); }
```

##### How to fix
Use within valid operational code.

---

### 0xC1159 - *Key Not Unique*

##### Description
The same key is used multiple times when setting parameters.

##### Example

```montiarc
component A { A a(x=1, x=2); }
```

---

### 0xC1174 - *Connectors In Atomic*

##### Description
An atomic component is one without subcomponents; connectors cannot be used inside such components.

##### Example

```montiarc
component A {
  port out Integer o;
  port in  Integer i;
  // connectors forbidden in atomic
  o -> i;
}
```

---

### 0xC1175 - *Type Parameter Upper Case*

##### Description
Type parameters should start with an uppercase letter by convention.

##### Example

```montiarc
component A<t> { }
```

##### How to fix
Rename the type parameter to an uppercase identifier:

```montiarc
component A<T> { }
```

---

### 0xC1176 - *Type Argument Ignores Upper Bound*

##### Description
Type parameter does not respect its declared upper bound.

##### Example

```montiarc
component A<T extends Number> { }
component B { A<String> inst; } // ❌ String does not extend Number
```

---

### 0xC1177 - *Too Few Type Arguments*

##### Description
Too few type arguments provided for a generic component.

##### Example

```montiarc
component A<K, V> { }
component B { A<String> inst; } // ❌ missing second argument
```

---

### 0xC1178 - *Too Many Type Arguments*

##### Description
Too many type arguments provided for a generic component.

##### Example

```montiarc
component A<T> { }
component B { A<String, Integer> inst; } // two args for one parameter
```

---

### 0xC1182 - *Raw Use Of Parametrized Type*

##### Description
Raw usage of a parameterized component type without type arguments.

##### Example

```montiarc
component A<T> { }
component B { A inst; } // raw use
```

---

### 0xC1184 - *Refinement Port Name Mismatch*

##### Description
Interface mismatch during refinement: a port exists in the abstraction but not in the refinement.

##### Example

```montiarc
component Base { port in Integer x; }
component Sub refines Base { 
  // missing port x 
  port in Integer y;
}
```

---

### 0xC1185 - *Refinement Port Direction Changed*

##### Description
Direction mismatch: a port changes from `in` to `out` or vice versa during refinement.

##### Example

```montiarc
component Base { port in Integer x; }
component Sub refines Base { port out Integer x; }
```


---

### 0xC1186 - *Refinement Timing Mismatch In*

##### Description
Illegal timing change for an incoming port during refinement.

##### Example

```montiarc
component Base { port sync in Integer i; }
component Sub refines Base { port in Integer i; }
```

---

### 0xC1187 - *Refinement Timing Mismatch Out*

##### Description
Illegal timing change for an outgoing port during refinement.

##### Example

```montiarc
component Base { port sync out Integer o; }
component Sub refines Base { port out Integer o; }
```

---

### 0xC1188 - *Refinement In Port Type Mismatch*

##### Description
Input port type in the refinement is not a supertype of the abstraction’s port type.

##### Example

```montiarc
component Base { port in Object o; }
component Sub refines Base { port in String o; }
```

---

### 0xC1189 - *Refinement Out Port Type Mismatch*

##### Description
Output port type in the refinement is not a subtype of the abstraction’s port type.

##### Example

```montiarc
component Base { port out String o; }
component Sub refines Base { port out Object o; }
```

---

### 0xC1190 - *Circular Fields Dependency*

##### Description
Component fields depend on each other circularly in their initializers.

##### Example

```montiarc
component A {
  Integer a = b;
  Integer b = a;
}
```

---

### 0xC1191 - *IN Port Ref In Invalid Context*

##### Description
Value of an incoming port is not available in the given static context.

##### Example

```montiarc
component A {
  port in int i;
  int x = i; // cannot read port in initializer
}
```

##### How to fix
Read port values at runtime (e.g., in behavior) or pass them through connectors.

---

### 0xC1192 - *Out Port Member Accessed*

##### Description
Cannot access an outgoing port or its members; output ports are write-only.

##### Example

```montiarc
component A {
  port out Integer o;
  compute {
    int x = o; // cannot read output port
  }
}
```

##### How to fix
Use the port as a connector source only, or read through an input port.

---

### 0xC1193 - *Invalid Port Timing Override*

##### Description
A port cannot override its timing annotation to an incompatible value.

##### Example

```montiarc
component Base { port in Integer o; }
component Sub extends Base { port sync in Integer o; }
```
