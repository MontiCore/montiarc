<!-- (c) https://github.com/MontiCore/monticore -->

```cd4code
package corp;
import java.util.Date;

classdiagram MyCompany {

  enum CorpKind { SOLE_PROPRIETOR, S_CORP, C_CORP, B_CORP, CLOSE_CORP, NON_PROFIT; }
  abstract class Entity;
  package people {
    class Person extends Entity {
      Date birthday;
      List<String> nickNames;
      -> Address [*] {ordered};
    }
    class Address {
      String city;
      String street;
      int number;
    }
  }
  class Company extends Entity {
    CorpKind kind;
  }
  class Employee extends people.Person {
    int salary;
  }
  class Share {
    int value;
  }
  association [1..*] Company (employer) <-> Employee [*];
  composition [1] Company <- Share [*];
  association shareholding [1] Entity (shareholder) -- (owns) Share [*];
}
```
UML class diagrams are a handy way to define object oriented types.
MontiArc has an integration for class diagrams written with [_cd4code_][cd4analysis] (a language extension of _cd4analysis_).
cd4code features the definition of
* Enums
* Interfaces
* Classes
* Abstract classes
* Field members
* Method members
* Associations between classes, also featuring cardinalities
* Package structures

## Comprehensive information
You can find comprehensive information on how to define types with cd4code [here][cd4analysis].
Moreover there are [several examples][examples] of cd4code models.

## Special behavior of class diagram usage in MontiArc
There are some things that you should note when working with object oriented types in MontiArc:

### Packages
Class diagrams themselves are defined within a package structure.
However, they themselves also span a new package.
E.g., take the following class diagram model:
```cd4code
package com.example;

classdiagram fingerprotection {
  public enum FingerProtectionOrder {
    PROTECT, PROTECTION_OFF;
  }
}
```
The `FingerProtectionOrder` type will be located in the `com.example.fingerprotection` package.
Note that the class diagram name becomes a part of the package name.
To use the type in MontiArc, one would be importing it at the beginning of the MontiArc model:
```montiarc
package com.example.window;

import com.example.fingerprotection.FingerProtectionOrder;

component WindowController { /*...*/ }
```

### Calling constructors
```montiarc
compute {
  personOutPort = Person.Person("Steven");
}
```
When calling constructors of object oriented types in MontiArc, one calls the constructor as if it was a method of the defining type:
```java
TypeName.methodName(/*arguments*/)
```
As the constructor method name is the same as the type name, this syntax becomes
```java
TypeName.TypeName(/*arguments*/)
```

