---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Java Integration (a.k.a. Class2MC)

When enabled ([Gradle](../../Usage/Gradle/index.md#using-java-types) / [CLI](../../Usage/CLI/MontiArc.md)), Java
types can be used within MontiArc models, just like user-defined types.

## Special behavior of Java type usage in MontiArc
Note that calling constructors of object-oriented types in MontiArc obeys a special syntax:
```montiarc
compute {
  personOutPort = Person.Person("Steven");
}
```
When calling constructors, one calls the constructor as if it were a method of the defining type:
```java
TypeName.methodName(/*arguments*/)
```
As the constructor method name is the same as the type name, this syntax becomes
```java
TypeName.TypeName(/*arguments*/)
```
