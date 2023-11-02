<!-- (c) https://github.com/MontiCore/monticore -->
This page subsumes all available operators that are used to calculate new values from other ones.

Table of contents:
{{< toc format=html >}}

## Arithmetic operators
Where operands and results are both numeric.
Note that _char_ is also considered a numeric type.
| Operator(s) | Description |
|-------------|-------------|
| `a + b` | Addition |
| `a - b` | Subtraction |
| `a * b` | Multiplication |
| `a / b` | Division. If both operands a and b are integer types, then the result is rounded down to the next integer number. |
| `a % b` | Modulo |
| `+a` | Positive prefix (does not change the value) |
| `-a` | Negative prefix (same as `-1 * a`) |
| `~a` | Bitwise complement of a numeric number |
| `&a` | Bitwise _AND_ |
| `^a` | Bitwise _XOR_ |
| `\|a` | Bitwise _OR_ |    
| `a << n` | Signed binary left shift of `a` by `n` bits (the sign of the number is preserved, the operation is equivalent with a multiplication by 2^n) |
| `a >> n` | Signed binary right shift of `a` by `n` bits (the sign of the number is preserved, the operation is equivalent with a division by 2^n) |
| `a >>> n` | Unsigned binary right shift of `a` by `n` bits (the sign bit is not preserved and the number is padded with 0s) |

Note that if there is only one operand or if both operands are of the same type, then the result of the operation has the same type.
Else, the result is of the _wider_ type.
The following is a ranking of the _wideness_ of numeric types:
1. double
2. float
3. long
4. int
5. char

## Boolean operators
Where operands and results are both boolean.
| Operator(s) | Description |
|-------------|-------------|
| `!a` | Negates the boolean value `a` |
| `a & b` | Logical _AND_ operation, evaluating both expressions `a` and `b` |
| `a && b` | Shortcutted logical _AND_ operation: If `a` already evaluates to `false`, then `b` is not evaluated. |
| `a \| b` | Logical _OR_ operation, evaluating both expressions `a` and `b` |
| `a \|\| b` | Shortcutted logical _OR_ operation: If `a` already evaluates to `true`, then `b` is not evaluated.  |

## General comparison operators
| Operator(s) | Description |
|-------------|-------------|
| `a == b` | Test for equality |
| `a != b` | Test for inequality |

Note that for primitive types equality is based on the equality of their values.
For object oriented types, the equality test is based on whether the operands have the reference to the same object.

## Numeric comparison operators
Where operands are numeric and the result is boolean
| Operator(s) | Description |
|-------------|-------------|
| `a < b` | Test whether `a` is smaller than `b` |
| `a <= b` | Test whether `a` is smaller or equal to `b` |
| `a > b` | Test whether `a` is bigger than `b` |
| `a >= b` | Test whether `a` is bigger or equal to `b` |

## Field value manipulating operators
| Operator(s) | Description |
|-------------|-------------|
| `++a` | Increases the value of `a` by one and then returns `a`'s new value. |
| `--a` | Decreases the value of `a` by one and then returns `a`'s new value. |
| `a++` | Increases the value of `a` by one, but returns its old value (before the increase). |
| `a--` | Decreases the value of `a` by one, but returns its old value (before the decrease). |
| `a = b` | `b`'s value is assigned to `a` and then `a`'s new value is returned. |
| `a += b` | Assigns the result of  `a + b` to `a` and returns `a`'s new value. |
| `a -= b` | Assigns the result of  `a - b` to `a` and returns `a`'s new value. |
| `a *= b` | Assigns the result of  `a * b` to `a` and returns `a`'s new value. |
| `a /= b` | Assigns the result of  `a / b` to `a` and returns `a`'s new value. |
| `a %= b` | Assigns the result of  `a % b` to `a` and returns `a`'s new value. |
| `a &= b` | Assigns the result of  `a & b` to `a` and returns `a`'s new value. |
| `a \|= b` | Assigns the result of  `a \| b` to `a` and returns `a`'s new value. |
| `a ^= b` | Assigns the result of  `a ^ b` to `a` and returns `a`'s new value. |
| `a <<= b` | Assigns the result of  `a << b` to `a` and returns `a`'s new value. |
| `a >>= b` | Assigns the result of  `a >> b` to `a` and returns `a`'s new value. |
| `a >>>= b` | Assigns the result of  `a >>> b` to `a` and returns `a`'s new value. |

Note that these operations are illegal on ports, as incoming ports are read-only and outgoing ports are write-only.

## Other operators
| Operator(s) | Description |
|-------------|-------------|
| `"Hello" + " " + "World"` | String concatenation (if Strings are available, e.g. by using [_class2mc_]) |
| `foo.someField` | Access to the field `someField` of the object `foo` (given that `someField` exists in `foo`) |
| `foo.someMethod(arg1, arg2)` | Method call to `someMethod` of the object `foo` (given that `someMethod` exists in `foo`) |
| `booleanExpr ? a : b` | If `booleanExpr` evaluates to `true`, then `a` is evaluated and its value is returned. If `booleanExpr` evaluates to `false`, then `b` is evaluated and its value is returned. |
| `(a)` | Bracket expression that just returns `a`. This expression is used to set the evaluation order of composed expressions: `(a + b) * c` is evaluated as _c times the sum of a and b_, while `a + b * c` is evaluated as _a added to the product of b and c_. | 
