<!-- (c) https://github.com/MontiCore/monticore -->
# Statements

Actions are defined by a list of statements that are instructions to be 
executed in some sequence. A statement can include one or more expressions.

## Variable declarations
Declaration statements declare variables and constants, specifying their data 
type, name, and initial value. Some examples are given in the following:.

```java
int a = 42;
```
The declaration statement above declares a variable named `a` of type `int` 
and assigns it the value `42`.

```java
String text = "Hello";
```

The declaration statement above declares a variable named `text` of type 
`String` and assigns it the value `"Hello"`.

A variable data type may also be some object type.

```java
Person person = Person.Person();
```

The declaration statement above declares a variable named `person` of type 
`Parson` and assigns the variable to a new object of type `Person`;

## Expression statements
```java
x = 14;
foo.callToAMethod();
```
One can always use an expression as a statement.
Thus, assignments can be declared (which are formally expressions) and methods can be called.

## Statement block
```java
x = 15;
{
  int y = 13;
  foo.someMethodCall();
}
```
Statement blocks can be used to structure code.
Usually, they are used for structuring control statements.
On the other side, one can instead use single statements, such as
```java
if (condition) thenStatement;
else elseStatement;
```
But this is discouraged.

## Control statements

### If-else
```java
if (condition) {
  // Statements
} else {
  // Statements
}
```

### While loop
```java
while (condition) {
  // Statements
}
```

### Do-while loop
```java
do {
  // Statements
} while (condition)
```
First executes the statements block following the `do`.
After each execution of the statement block, the condition is evaluated.
If it evaluates to `true`, then the statement block is executed again.

### For loop
There are three different versions of the for loop.
```java
// Creating control variables:
for (int i = expr, j = expr; condition; i++, j--) {
  // Statements
}
```
First, new variables are created.
The condition that decides whether the next round of the loop should be executed is declared following a semicolon.
After another semicolon, expressions are defined that are executed at the end of each loop.

```java
// Initializing existing control variables
for (i = expr, j = expr; condition; i++, j--) {
  // Statements
}
```
Same as before, but the control variables already exist.

```java
// For-iteration
for (int val : IntCollection) {
  // Statements
}
```
Iterating over a collection of items.

### Switch
```java
switch (expression) {
  case CONSTANT_OR_ENUM_VAL: /* statements */ break;
  default: /* statements */ break;
}
```
evaluates the expression and depending on whether it is equal to a constant or 
enumeration value, it executes the statements declared within the respective cases.
These statement definitions must be terminated with a `break;` statement.
The statements of the `default` case are executed if the evaluated expression 
value does not match any other case.

### Break
The `break;` statement is not only available in `switch` statements, but also in loops in which they terminate the loop early.
