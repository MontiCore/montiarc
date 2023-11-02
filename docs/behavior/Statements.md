This page summarizes the available statements in MontiArc.
Note that the are not available everywhere (e.g. they are not allowed directly
 in component bodies), but usually where behavior is defined.

## Operational statements

### Variable declarations
```java
DataType varName;
DataType varName = initValue;
DataType var1Name, varName2 = initVal, varName3;
```
Variables are declared by first defining their type
and then creating a name for them.
Usually, one assigns an initial value to the variable, but this is optional.
Moreover, one can define multiple variables of the same type within the same 
statement, separating them with commas.

### Expression statements
```java
x = 14;
foo.callToAMethod();
```
One can always use an expression as a statement.
Thus, assignments can be declared (which are formally expressions) and methods can be called.

### Statement block
```java
x = 15;
{
  int y = 13;
  foo.someMethodCall();
}
```
Statement blocks can be used to structure code.
Usually they are used to structure control statements.
On the other side, one can instead use single statements, such as
```java
if (condition) thenStatement;
else elseStatement;
```
but this is discouraged.

### Empty statement
```java
;
```
It is possible to use the semicolon as a statement to signify that nothing should be done as part of the statement.

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

### break
The `break;` statement is not only available in `switch`es, but also in loops in 
which they terminate the loop early.
