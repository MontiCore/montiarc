---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Handwritten Code

Handwritten code can be integrated into the simulator using the TOP mechanism.
Any generated class can be overridden by creating a class with the same name in the source set.
The generator will detect the handwritten file and generate the original with a suffix of `TOP`.

For an atomic component, you can add handwritten behavior by using the TOP mechanism
on an empty [compute block](../Behavior/Compute.md).

The following component:
```montiarc
component Atomic {
  compute {}
}
```

Would normally generate a class called `AtomicCompute`. But if you add your own `AtomicCompute` in the Java sources, 
the former will be named `AtomicComputeTOP` which you will have to inherit. All other references will then point
to your `AtomicCompute` class. 


```java
public class AtomicCompute extends AtomicComputeTOP {

}
```

You can now override all the methods a behavior has:

| Method                                                        | Description                                                                                                                                   |
| ------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `init()`                                                      | Called before the simulation starts. It can be used to send initial values on (delayed) ports. Must end with sending a tick on delayed ports. |
| `tick(<ComponentName>SyncMsg msg)`                            | Called on every tick. The default behavior is to unwrap the `msg` and call `realTick`.                                                        |
| `realTick(<syncPortType1> port1, <syncPortType2> port2, ...)` | The action executed on a tick. It has the sync port values as parameters.                                                                     |
| `msg_<PortName>(<portType> value)`                            | Called whenever an event message is received on `<PortName>`.                                                                                 |
