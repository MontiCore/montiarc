<!-- (c) https://github.com/MontiCore/monticore -->

# Expressions

Expressions are a combination of variables, operations, and method invocations 
that evaluate to some value. Expressions can be used in arguments of component 
instantiations, in guards of transitions, and as part of statements in actions 
of automata.

Given the following types 

```
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

and imports of these types 

```
import montiarc.types.Person;
import montiarc.types.Role;
```

the following are valid expressions

## Object creation 

```
Person p = Person.Person();
```

`Person p` declares a variable of type Person
and `Person.Person()` creates the corresponding object.

## Field access

`p.age = 35;` 

accesses the field `age` of variable `p` and sets its value to `35`.

`p.name = "Max";`

accesses the field `name` of variable `p` and sets its value to `"Max"`

`p.role = Role.Role();`

access the field `role` of variable `p` and creates a Object of type `Role` 

`p.role.id = 5; ` 

access the field `id` of field `role` of variable `p` and sets it to `5`

`int age2 = p.age;` 

accesses field `age` of variable `p` and stores it in a new variable `age2` 

## Method invocation

`p.getFullName()` 

invokes method `getFullName()` on the object behind variable `p`

## Arithmetic Operators

Arithmetic operators perform mathematical operations and return a number.

| Operator | Name           | Example |
|----------|----------------|---------|
| +        | Addition       | a + b   |
| -        | Subtraction    | a - b   |
| *        | Multiplication | a * b   |
| /        | Division       | a / b   |
| %        | Modulo         | a % b   |

## Assignment Operators

Assignment operators assign a value to a variable or send a message via a port.

| Operator | Example | Abbreviation for |
|----------|---------|------------------|
| =        | a = b   |                  |
| +=       | a += b  | a = a + b        |
| -=       | a -= b  | a = a - b        |
| *=       | a *= b  | a = a * b        |
| /=       | a /= b  | a = a / b        |
| %=       | a %= b  | a = a % b        |

## Comparison Operators

Comparison operators compare two values and return a boolean.

| Operator | Name                  | Example |
|----------|-----------------------|---------|
| ==       | Equal to              | a == b  |
| !=       | Not equal to          | a != b  |
| \>       | Greater than          | a > b   |
| <        | Less than             | a < b   |
| \>=      | Greater than or equal | a >= b  |
| <=       | Less than or equal    | a <= b  |

## Logical Operators

Logical operators operate on boolean values and return a boolean.

| Operator | Name        | Example  |
|----------|-------------|----------|
| &&       | Logical and | a && b   |
| \|\|     | Logical or  | a \|\| b |
| !        | Logical not | !a       |