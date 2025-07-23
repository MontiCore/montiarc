---
icon: material/test-tube
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Writing Automated Tests

An important approach to ensuring system quality is systematic testing.
Testing is the act of detecting failures in a product.
A failure is a divergence between the expected and actual behavior of software.
MontiArc includes support for writing automated tests using the [simulator](../../Usage/Simulation/index.md).
These can be run whenever changes are made to ensure that the systems behaves as specified.

There are multiple testing frameworks available:

- [MaUnit](./MaUnit.md): Write tests in the MontiArc modeling language
- [Sequence Diagrams](./SequenceDiagrams.md): Write tests using sequence diagrams
- [JUnit](./JUnit.md): Write tests as plain Java tests