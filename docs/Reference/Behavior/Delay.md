---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Delayed Behaviors

To break up [feedback loops](../Component/Connectors.md#feedback), behaviors can be declared delaying.

Any behavior can be made delaying by adding the `<<delayed>>` stereotype to it.
Resulting in all output ports being delayed by one time slice. 

```montiarc
<<delayed>> behaviorType {}
```