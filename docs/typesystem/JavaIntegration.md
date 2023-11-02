<!-- (c) https://github.com/MontiCore/monticore -->
When enabling the `class2mc` option, then the Java standard library is available in all MontiArc models.

## Special behavior of Java type usage in MontiArc
Note that calling constructors of object oriented types in MontiArc obeys a special syntax:
```montiarc
compute {
  personOutPort = Person.Person("Steven");
}
```
When calling constructors, one calls the constructor as if it was a method of the defining type:
```java
TypeName.methodName(/*arguments*/)
```
As the constructor method name is the same as the type name, this syntax becomes
```java
TypeName.TypeName(/*arguments*/)
```
