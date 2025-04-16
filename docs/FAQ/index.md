---
hide:
  - navigation
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# FAQ

Read through some of the most frequently asked questions:

??? quote "I hate MontiArc; what can I do?"
    Well, MBSE is not for everyone. Maybe take a C lesson next?

??? quote "I got the error 0xC1117 and don't know how to fix it"
    The error tells you that you have a non-delayed [feedback loop](../Reference/Component/Connectors.md#feedback).
    This means the inputs of a component directly or indirectly depend on its own outputs.
    To fix this, [add a delay](../Reference/Component/Connectors.md#feedback) somewhere in the loop.

??? quote "When should I use event ports vs sync ports?"
    Depending on what you want to model you can choose different [timings](../Reference/Concepts/Timing.md).
    <br/>Here is a quick overview of when to choose a timing:

    [**Timed**](../Reference/Concepts/Timing.md#timed)
    
    - Perfect for asynchronous communication
    - Discrete events that occur never, once, or multiple times inside a time frame
    - *Ask yourself*: Do I model Software?
    
    [**Synchronous**](../Reference/Concepts/Timing.md#synchronous)

    - Discrete messages that are sent once per time frame
    - All messages are sent and received at the same time
    - *Ask yourself*: Do I model Hardware or Software that is continuously updated?

??? quote "I want to read from two event ports at the same time"
    Think about why you want to do that, maybe event ports are not the right approach to your problem.
    To solve this, you can:
    
    - Make the ports [synchronous](../Reference/Concepts/Timing.md)
    - Use a [component field](../Reference/Behavior/Fields.md) to buffer the events.