<!-- (c) https://github.com/MontiCore/monticore -->
# Expressions

Expressions are combinations of variables, literals, operators, and method invocations that evaluate to a single value.
They are used throughout the language, such as in arguments for component instantiations, in guards of transitions, and as part of statements in the actions of automata.

## Context & Setup

To illustrate how expressions work, assume the following types are defined:

```classdiagram
  public class Person {
    public int age;
    public String name;
    public Role role;
    public String getFullName();
  }

  public class Role {
    public int id;
  }
```

And imported into your current model:

```montiarc
import montiarc.types.Person;
import montiarc.types.Role;
```

## Working with Objects

The following examples demonstrate how to use expressions to create objects, access fields, and invoke methods.

```java
// 1. Object Creation
// Declares a variable 'p' of type Person and evaluates the creation expression
Person p = Person.Person();

// 2. Field Access & Assignment 
// Accesses fields on variable 'p' and assigns new values to them
p.age = 35;
p.name = "Max";

// Creates a new Role object and assigns it to the 'role' field
p.role = Role.Role();

// Chained field access: accesses the 'id' field of the 'role' field
p.role.id = 5; 

// 3. Field Reading
// Accesses the 'age' field and stores its evaluated result in a new variable
int currentAge = p.age;

// 4. Method Invocation
// Invokes the getFullName() method on the object referenced by 'p'
p.getFullName();

```

## Operators

The language supports standard mathematical, assignment, comparison, and logical operators.

### Arithmetic Operators

Arithmetic operators perform mathematical calculations and evaluate to a numeric value.

| Operator | Name           | Example |
| -------- | -------------- | ------- |
| `+`      | Addition       | `a + b` |
| `-`      | Subtraction    | `a - b` |
| `*`      | Multiplication | `a * b` |
| `/`      | Division       | `a / b` |
| `%`      | Modulo         | `a % b` |

### Assignment Operators

Assignment operators evaluate the right side and assign the resulting value to a variable on the left side, or send a message via a port.

| Operator | Example  | Abbreviation for |
| -------- | -------- | ---------------- |
| `=`      | `a = b`  |                  |
| `+=`     | `a += b` | `a = a + b`      |
| `-=`     | `a -= b` | `a = a - b`      |
| `*=`     | `a *= b` | `a = a * b`      |
| `/=`     | `a /= b` | `a = a / b`      |
| `%=`     | `a %= b` | `a = a % b`      |

### Comparison Operators

Comparison operators compare two evaluated values and return a Boolean (`true` or `false`).

| Operator | Name                  | Example  |
| -------- | --------------------- | -------- |
| `==`     | Equal to              | `a == b` |
| `!=`     | Not equal to          | `a != b` |
| `>`      | Greater than          | `a > b`  |
| `<`      | Less than             | `a < b`  |
| `>=`     | Greater than or equal | `a >= b` |
| `<=`     | Less than or equal    | `a <= b` |

### Logical Operators

Logical operators operate strictly on Boolean values and evaluate to a Boolean result.

| Operator | Name        | Example  |
| -------- | ----------- | -------- |
| `&&`     | Logical AND | `a && b` |
| `        |             | `        |
| `!`      | Logical NOT | `!a`     |