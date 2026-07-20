/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc.util.MCError;
import montiarc._symboltable.TransitiveScopeSetter;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BYTE;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.CHAR;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.DOUBLE;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.FLOAT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.INT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.LONG;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.SHORT;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class provides tests for validating the correctness of type checking of
 * primitives in expressions.
 */
public class TypeCheckPrimitivesTest extends MontiArcTestBase {

  /**
   * The enclosing scope of the symbols of the test setup
   */
  protected IArcBasisScope scope;

  @BeforeEach
  protected void initSymbols() {
    this.scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addSubScope(this.scope);
    this.scope.setEnclosingScope(MontiArcMill.globalScope());
    FieldSymbol aBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aBoolean")
      .setType(createPrimitive(BOOLEAN))
      .build();
    SymbolService.link(this.scope, aBoolean);
    FieldSymbol aChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aChar")
      .setType(createPrimitive(CHAR))
      .build();
    SymbolService.link(this.scope, aChar);
    FieldSymbol aByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aByte")
      .setType(createPrimitive(BYTE))
      .build();
    SymbolService.link(this.scope, aByte);
    FieldSymbol aShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aShort")
      .setType(createPrimitive(SHORT))
      .build();
    SymbolService.link(this.scope, aShort);
    FieldSymbol anInt = MontiArcMill.fieldSymbolBuilder()
      .setName("anInt")
      .setType(createPrimitive(INT))
      .build();
    SymbolService.link(this.scope, anInt);
    FieldSymbol aLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aLong")
      .setType(createPrimitive(LONG))
      .build();
    SymbolService.link(this.scope, aLong);
    FieldSymbol aFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aFloat")
      .setType(createPrimitive(FLOAT))
      .build();
    SymbolService.link(this.scope, aFloat);
    FieldSymbol aDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aDouble")
      .setType(createPrimitive(DOUBLE))
      .build();
    SymbolService.link(this.scope, aDouble);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "aBoolean = true", // expected boolean and provided boolean
    "aBoolean = false", // expected boolean and provided boolean
    "aBoolean = aBoolean", // expected boolean and provided boolean
    "aChar = 'a'", // expected char and provided char
    // "aChar = 0", // expected char and provided char (min value)
    // "aChar = +1", // expected char and provided char (int conversion)
    // "aChar = 65535", // expected char and provided char (max value)
    "aChar = aChar", // expected char and provided char
    // "aByte = 'a'", // expected byte and provided byte (char conversion)
    // "aByte = 0", // expected byte and provided byte (int conversion)
    // "aByte = +1", // expected byte and provided byte (int conversion)
    // "aByte = -1", // expected byte and provided byte (int conversion)
    // "aByte = 127", // expected byte and provided byte (max value)
    // "aByte = -128", // expected byte and provided byte (min value)
    "aByte = aByte", // expected byte and provided byte
    // "aShort = 'a'", //  expected short and provided short (char conversion)
    // "aShort = 0", // expected short and provided short (int conversion)
    // "aShort = +1", // expected short and provided short (int conversion)
    // "aShort = -1", // expected short and provided short (int conversion)
    // "aShort = 32767", // expected short and provided short (max value)
    // "aShort = -32768", // expected short and provided short (min value)
    "aShort = aByte", // expected short and provided short (byte conversion)
    "aShort = aShort", // expected short and provided short
    "anInt = 'a'", // expected int and provided int (char conversion)
    "anInt = 0", // expected int and provided int
    "anInt = +1", // expected int and provided int
    "anInt = -1", // expected int and provided int
    "anInt = 2147483647", // expected int and provided int (max value)
    "anInt = -2147483648", // expected int and provided int (min value)
    "anInt = aChar", // expected int and provided int (char conversion)
    "anInt = aByte", // expected int and provided int (byte conversion)
    "anInt = aShort", // expected int and provided int (short conversion)
    "anInt = anInt", // expected int and provided int
    "aLong = 'a'", // expected long and provided long (char conversion)
    "aLong = 0", // expected long and provided long (int conversion)
    "aLong = 0l", // expected long and provided long
    "aLong = +1l", // expected long and provided long
    "aLong = -1l", // expected long and provided long
    "aLong = 9223372036854775807l", // expected long and provided long (max value))
    "aLong = -9223372036854775808l", // expected long and provided long (min value))
    "aLong = aChar", // expected long and provided long (char conversion)
    "aLong = aByte", // expected long and provided long (byte conversion)
    "aLong = aShort", // expected long and provided long (short conversion)
    "aLong = anInt", // expected long and provided long (int conversion)
    "aLong = aLong", // expected long and provided long
    "aFloat = 'a'", // expected float and provided float (char conversion)
    "aFloat = 0", // expected float and provided float (int conversion)
    "aFloat = 0l", // expected float and provided float (long conversion)
    // "aFloat = 0f", // expected float and provided float
    // "aFloat = 0.f", // expected float and provided float
    "aFloat = 0.0f", // expected float and provided float
    "aFloat = +0.1f", // expected float and provided float
    "aFloat = -0.1f", // expected float and provided float
    "aFloat = aChar", // expected float and provided float (char conversion)
    "aFloat = aByte", // expected float and provided float (byte conversion)
    "aFloat = aShort", // expected float and provided float (short conversion)
    "aFloat = anInt", // expected float and provided float (int conversion)
    "aFloat = aLong", // expected float and provided float (long conversion)
    "aFloat = aFloat", // expected float and provided float
    "aDouble = 'a'", // expected double and provided double (char conversion)
    "aDouble = 0", // expected double and provided double (int conversion)
    "aDouble = 0l", // expected double and provided double (long conversion)
    "aDouble = 0.0f", // expected double and provided double (float conversion)
    "aDouble = 0.0", // expected double and provided double
    "aDouble = +0.1", // expected double and provided double
    "aDouble = -0.1", // expected double and provided double
    "aDouble = aChar", // expected double and provided double (char conversion)
    "aDouble = aByte", // expected double and provided double (byte conversion)
    "aDouble = aShort", // expected double and provided double (short conversion)
    "aDouble = anInt", // expected double and provided double (int conversion)
    "aDouble = aLong", // expected double and provided double (long conversion)
    "aDouble = aFloat", // expected double and provided double (float conversion)
    "aDouble = aDouble", // expected double and provided double
    "aChar += aChar", // += applicable to char, char
    "aChar += aByte", // += applicable to char, byte
    "aChar += aShort", // += applicable to char, short
    "aChar += anInt", // += applicable to char, int
    "aChar += aLong", // += applicable to char, long
    "aChar += aFloat", // += applicable to char, float
    "aChar += aDouble", // += applicable to char, double
    "aShort += aChar", // += applicable to short, char
    "aShort += aByte", // += applicable to short, byte
    "aShort += aShort", // += applicable to short, short
    "aShort += anInt", // += applicable to short, int
    "aShort += aLong", // += applicable to short, long
    "aShort += aFloat", // += applicable to short, float
    "aShort += aDouble", // += applicable to short, double
    "anInt += aChar", // += applicable to int, char
    "anInt += aByte", // += applicable to int, byte
    "anInt += aShort", // += applicable to int, short
    "anInt += anInt", // += applicable to int, int
    "anInt += aLong", // += applicable to int, long
    "anInt += aFloat", // += applicable to int, float
    "anInt += aDouble", // += applicable to int, double
    "aFloat += aChar", // += applicable to float, char
    "aFloat += aByte", // += applicable to float, byte
    "aFloat += aShort", // += applicable to float, short
    "aFloat += anInt", // += applicable to float, int
    "aFloat += aLong", // += applicable to float, long
    "aFloat += aFloat", // += applicable to float, float
    "aFloat += aDouble", // += applicable to float, double
    "aDouble += aChar", // += applicable to double, char
    "aDouble += aByte", // += applicable to double, byte
    "aDouble += aShort", // += applicable to double, short
    "aDouble += anInt", // += applicable to double, int
    "aDouble += aLong", // += applicable to double, long
    "aDouble += aFloat", // += applicable to double, float
    "aDouble += aDouble", // += applicable to double, double
    "aChar -= aChar", // -= applicable to char, char
    "aChar -= aByte", // -= applicable to char, byte
    "aChar -= aShort", // -= applicable to char, short
    "aChar -= anInt", // -= applicable to char, int
    "aChar -= aLong", // -= applicable to char, long
    "aChar -= aFloat", // -= applicable to char, float
    "aChar -= aDouble", // -= applicable to char, double
    "aShort -= aChar", // -= applicable to short, char
    "aShort -= aByte", // -= applicable to short, byte
    "aShort -= aShort", // -= applicable to short, short
    "aShort -= anInt", // -= applicable to short, int
    "aShort -= aLong", // -= applicable to short, long
    "aShort -= aFloat", // -= applicable to short, float
    "aShort -= aDouble", // -= applicable to short, double
    "anInt -= aChar", // -= applicable to int, char
    "anInt -= aByte", // -= applicable to int, byte
    "anInt -= aShort", // -= applicable to int, short
    "anInt -= anInt", // -= applicable to int, int
    "anInt -= aLong", // -= applicable to int, long
    "anInt -= aFloat", // -= applicable to int, float
    "anInt -= aDouble", // -= applicable to int, double
    "aFloat -= aChar", // -= applicable to float, char
    "aFloat -= aByte", // -= applicable to float, byte
    "aFloat -= aShort", // -= applicable to float, short
    "aFloat -= anInt", // -= applicable to float, int
    "aFloat -= aLong", // -= applicable to float, long
    "aFloat -= aFloat", // -= applicable to float, float
    "aFloat -= aDouble", // -= applicable to float, double
    "aDouble -= aChar", // -= applicable to double, char
    "aDouble -= aByte", // -= applicable to double, byte
    "aDouble -= aShort", // -= applicable to double, short
    "aDouble -= anInt", // -= applicable to double, int
    "aDouble -= aLong", // -= applicable to double, long
    "aDouble -= aFloat", // -= applicable to double, float
    "aDouble -= aDouble", // -= applicable to double, double
    "aChar *= aChar", // *= applicable to char, char
    "aChar *= aByte", // *= applicable to char, byte
    "aChar *= aShort", // *= applicable to char, short
    "aChar *= anInt", // *= applicable to char, int
    "aChar *= aLong", // *= applicable to char, long
    "aChar *= aFloat", // *= applicable to char, float
    "aChar *= aDouble", // *= applicable to char, double
    "aShort *= aChar", // *= applicable to short, char
    "aShort *= aByte", // *= applicable to short, byte
    "aShort *= aShort", // *= applicable to short, short
    "aShort *= anInt", // *= applicable to short, int
    "aShort *= aLong", // *= applicable to short, long
    "aShort *= aFloat", // *= applicable to short, float
    "aShort *= aDouble", // *= applicable to short, double
    "anInt *= aChar", // *= applicable to int, char
    "anInt *= aByte", // *= applicable to int, byte
    "anInt *= aShort", // *= applicable to int, short
    "anInt *= anInt", // *= applicable to int, int
    "anInt *= aLong", // *= applicable to int, long
    "anInt *= aFloat", // *= applicable to int, float
    "anInt *= aDouble", // *= applicable to int, double
    "aFloat *= aChar", // *= applicable to float, char
    "aFloat *= aByte", // *= applicable to float, byte
    "aFloat *= aShort", // *= applicable to float, short
    "aFloat *= anInt", // *= applicable to float, int
    "aFloat *= aLong", // *= applicable to float, long
    "aFloat *= aFloat", // *= applicable to float, float
    "aFloat *= aDouble", // *= applicable to float, double
    "aDouble *= aChar", // *= applicable to double, char
    "aDouble *= aByte", // *= applicable to double, byte
    "aDouble *= aShort", // *= applicable to double, short
    "aDouble *= anInt", // *= applicable to double, int
    "aDouble *= aLong", // *= applicable to double, long
    "aDouble *= aFloat", // *= applicable to double, float
    "aDouble *= aDouble", // *= applicable to double, double
    "aChar /= aChar", // /= applicable to char, char
    "aChar /= aByte", // /= applicable to char, byte
    "aChar /= aShort", // /= applicable to char, short
    "aChar /= anInt", // /= applicable to char, int
    "aChar /= aLong", // /= applicable to char, long
    "aChar /= aFloat", // /= applicable to char, float
    "aChar /= aDouble", // /= applicable to char, double
    "aShort /= aChar", // /= applicable to short, char
    "aShort /= aByte", // /= applicable to short, byte
    "aShort /= aShort", // /= applicable to short, short
    "aShort /= anInt", // /= applicable to short, int
    "aShort /= aLong", // /= applicable to short, long
    "aShort /= aFloat", // /= applicable to short, float
    "aShort /= aDouble", // /= applicable to short, double
    "anInt /= aChar", // /= applicable to int, char
    "anInt /= aByte", // /= applicable to int, byte
    "anInt /= aShort", // /= applicable to int, short
    "anInt /= anInt", // /= applicable to int, int
    "anInt /= aLong", // /= applicable to int, long
    "anInt /= aFloat", // /= applicable to int, float
    "anInt /= aDouble", // /= applicable to int, double
    "aFloat /= aChar", // /= applicable to float, char
    "aFloat /= aByte", // /= applicable to float, byte
    "aFloat /= aShort", // /= applicable to float, short
    "aFloat /= anInt", // /= applicable to float, int
    "aFloat /= aLong", // /= applicable to float, long
    "aFloat /= aFloat", // /= applicable to float, float
    "aFloat /= aDouble", // /= applicable to float, double
    "aDouble /= aChar", // /= applicable to double, char
    "aDouble /= aByte", // /= applicable to double, byte
    "aDouble /= aShort", // /= applicable to double, short
    "aDouble /= anInt", // /= applicable to double, int
    "aDouble /= aLong", // /= applicable to double, long
    "aDouble /= aFloat", // /= applicable to double, float
    "aDouble /= aDouble", // /= applicable to double, double
    "aChar %= aChar", // %= applicable to char, char
    "aChar %= aByte", // %= applicable to char, byte
    "aChar %= aShort", // %= applicable to char, short
    "aChar %= anInt", // %= applicable to char, int
    "aChar %= aLong", // %= applicable to char, long
    "aChar %= aFloat", // %= applicable to char, float
    "aChar %= aDouble", // %= applicable to char, double
    "aShort %= aChar", // %= applicable to short, char
    "aShort %= aByte", // %= applicable to short, byte
    "aShort %= aShort", // %= applicable to short, short
    "aShort %= anInt", // %= applicable to short, int
    "aShort %= aLong", // %= applicable to short, long
    "aShort %= aFloat", // %= applicable to short, float
    "aShort %= aDouble", // %= applicable to short, double
    "anInt %= aChar", // %= applicable to int, char
    "anInt %= aByte", // %= applicable to int, byte
    "anInt %= aShort", // %= applicable to int, short
    "anInt %= anInt", // %= applicable to int, int
    "anInt %= aLong", // %= applicable to int, long
    "anInt %= aFloat", // %= applicable to int, float
    "anInt %= aDouble", // %= applicable to int, double
    "aFloat %= aChar", // %= applicable to float, char
    "aFloat %= aByte", // %= applicable to float, byte
    "aFloat %= aShort", // %= applicable to float, short
    "aFloat %= anInt", // %= applicable to float, int
    "aFloat %= aLong", // %= applicable to float, long
    "aFloat %= aFloat", // %= applicable to float, float
    "aFloat %= aDouble", // %= applicable to float, double
    "aDouble %= aChar", // %= applicable to double, char
    "aDouble %= aByte", // %= applicable to double, byte
    "aDouble %= aShort", // %= applicable to double, short
    "aDouble %= anInt", // %= applicable to double, int
    "aDouble %= aLong", // %= applicable to double, long
    "aDouble %= aFloat", // %= applicable to double, float
    "aDouble %= aDouble", // %= applicable to double, double
    "aChar >>= aChar", // >>= applicable to char, char
    "aChar >>= aByte", // >>= applicable to char, byte
    "aChar >>= aShort", // >>= applicable to char, short
    "aChar >>= anInt", // >>= applicable to char, int
    "aChar >>= aLong", // >>= applicable to char, long
    "aByte >>= aChar", // >>= applicable to byte, char
    "aByte >>= aByte", // >>= applicable to byte, byte
    "aByte >>= aShort", // >>= applicable to byte, short
    "aByte >>= anInt", // >>= applicable to byte, int
    "aByte >>= aLong", // >>= applicable to byte, long
    "aShort >>= aChar", // >>= applicable to short, char
    "aShort >>= aByte", // >>= applicable to short, byte
    "aShort >>= aShort", // >>= applicable to short, short
    "aShort >>= anInt", // >>= applicable to short, int
    "aShort >>= aLong", // >>= applicable to short, long
    "anInt >>= aChar", // >>= applicable to int, char
    "anInt >>= aByte", // >>= applicable to int, byte
    "anInt >>= aShort", // >>= applicable to int, short
    "anInt >>= anInt", // >>= applicable to int, int
    "anInt >>= aLong", // >>= applicable to int, long
    "aLong >>= aChar", // >>= applicable to long, char
    "aLong >>= aByte", // >>= applicable to long, byte
    "aLong >>= aShort", // >>= applicable to long, short
    "aLong >>= anInt", // >>= applicable to long, int
    "aLong >>= aLong", // >>= applicable to long, long
    "aChar <<= aChar", // <<= applicable to char, char
    "aChar <<= aByte", // <<= applicable to char, byte
    "aChar <<= aShort", // <<= applicable to char, short
    "aChar <<= anInt", // <<= applicable to char, int
    "aChar <<= aLong", // <<= applicable to char, long
    "aByte <<= aChar", // <<= applicable to byte, char
    "aByte <<= aByte", // <<= applicable to byte, byte
    "aByte <<= aShort", // <<= applicable to byte, short
    "aByte <<= anInt", // <<= applicable to byte, int
    "aByte <<= aLong", // <<= applicable to byte, long
    "aShort <<= aChar", // <<= applicable to short, char
    "aShort <<= aByte", // <<= applicable to short, byte
    "aShort <<= aShort", // <<= applicable to short, short
    "aShort <<= anInt", // <<= applicable to short, int
    "aShort <<= aLong", // <<= applicable to short, long
    "anInt <<= aChar", // <<= applicable to int, char
    "anInt <<= aByte", // <<= applicable to int, byte
    "anInt <<= aShort", // <<= applicable to int, short
    "anInt <<= anInt", // <<= applicable to int, int
    "anInt <<= aLong", // <<= applicable to int, long
    "aLong <<= aChar", // <<= applicable to long, char
    "aLong <<= aByte", // <<= applicable to long, byte
    "aLong <<= aShort", // <<= applicable to long, short
    "aLong <<= anInt", // <<= applicable to long, int
    "aLong <<= aLong", // <<= applicable to long, long
    "aChar >>>= aChar", // >>>= applicable to char, char
    "aChar >>>= aByte", // >>>= applicable to char, byte
    "aChar >>>= aShort", // >>>= applicable to char, short
    "aChar >>>= anInt", // >>>= applicable to char, int
    "aChar >>>= aLong", // >>>= applicable to char, long
    "aByte >>>= aChar", // >>>= applicable to byte, char
    "aByte >>>= aByte", // >>>= applicable to byte, byte
    "aByte >>>= aShort", // >>>= applicable to byte, short
    "aByte >>>= anInt", // >>>= applicable to byte, int
    "aByte >>>= aLong", // >>>= applicable to byte, long
    "aShort >>>= aChar", // >>>= applicable to short, char
    "aShort >>>= aByte", // >>>= applicable to short, byte
    "aShort >>>= aShort", // >>>= applicable to short, short
    "aShort >>>= anInt", // >>>= applicable to short, int
    "aShort >>>= aLong", // >>>= applicable to short, long
    "anInt >>>= aChar", // >>>= applicable to int, char
    "anInt >>>= aByte", // >>>= applicable to int, byte
    "anInt >>>= aShort", // >>>= applicable to int, short
    "anInt >>>= anInt", // >>>= applicable to int, int
    "anInt >>>= aLong", // >>>= applicable to int, long
    "aLong >>>= aChar", // >>>= applicable to long, char
    "aLong >>>= aByte", // >>>= applicable to long, byte
    "aLong >>>= aShort", // >>>= applicable to long, short
    "aLong >>>= anInt", // >>>= applicable to long, int
    "aLong >>>= aLong", // >>>= applicable to long, long
    "aBoolean &= aBoolean", // &= applicable to boolean, boolean
    "aChar &= aChar", // &= applicable to char, char
    "aChar &= aByte", // &= applicable to char, byte
    "aChar &= aShort", // &= applicable to char, short
    "aChar &= anInt", // &= applicable to char, int
    "aChar &= aLong", // &= applicable to char, long
    "aByte &= aChar", // &= applicable to byte, char
    "aByte &= aByte", // &= applicable to byte, byte
    "aByte &= aShort", // &= applicable to byte, short
    "aByte &= anInt", // &= applicable to byte, int
    "aByte &= aLong", // &= applicable to byte, long
    "aShort &= aChar", // &= applicable to short, char
    "aShort &= aByte", // &= applicable to short, byte
    "aShort &= aShort", // &= applicable to short, short
    "aShort &= anInt", // &= applicable to short, int
    "aShort &= aLong", // &= applicable to short, long
    "anInt &= aChar", // &= applicable to int, char
    "anInt &= aByte", // &= applicable to int, byte
    "anInt &= aShort", // &= applicable to int, short
    "anInt &= anInt", // &= applicable to int, int
    "anInt &= aLong", // &= applicable to int, long
    "aLong &= aChar", // &= applicable to long, char
    "aLong &= aByte", // &= applicable to long, byte
    "aLong &= aShort", // &= applicable to long, short
    "aLong &= anInt", // &= applicable to long, int
    "aLong &= aLong", // &= applicable to long, long
    "aBoolean |= aBoolean", // |= applicable to boolean, boolean
    "aChar |= aChar", // |= applicable to char, char
    "aChar |= aByte", // |= applicable to char, byte
    "aChar |= aShort", // |= applicable to char, short
    "aChar |= anInt", // |= applicable to char, int
    "aChar |= aLong", // |= applicable to char, long
    "aByte |= aChar", // |= applicable to byte, char
    "aByte |= aByte", // |= applicable to byte, byte
    "aByte |= aShort", // |= applicable to byte, short
    "aByte |= anInt", // |= applicable to byte, int
    "aByte |= aLong", // |= applicable to byte, long
    "aShort |= aChar", // |= applicable to short, char
    "aShort |= aByte", // |= applicable to short, byte
    "aShort |= aShort", // |= applicable to short, short
    "aShort |= anInt", // |= applicable to short, int
    "aShort |= aLong", // |= applicable to short, long
    "anInt |= aChar", // |= applicable to int, char
    "anInt |= aByte", // |= applicable to int, byte
    "anInt |= aShort", // |= applicable to int, short
    "anInt |= anInt", // |= applicable to int, int
    "anInt |= aLong", // |= applicable to int, long
    "aLong |= aChar", // |= applicable to long, char
    "aLong |= aByte", // |= applicable to long, byte
    "aLong |= aShort", // |= applicable to long, short
    "aLong |= anInt", // |= applicable to long, int
    "aLong |= aLong", // |= applicable to long, long
    "aBoolean ^= aBoolean", // ^= applicable to boolean, boolean
    "aChar ^= aChar", // ^= applicable to char, char
    "aChar ^= aByte", // ^= applicable to char, byte
    "aChar ^= aShort", // ^= applicable to char, short
    "aChar ^= anInt", // ^= applicable to char, int
    "aChar ^= aLong", // ^= applicable to char, long
    "aByte ^= aChar", // ^= applicable to byte, char
    "aByte ^= aByte", // ^= applicable to byte, byte
    "aByte ^= aShort", // ^= applicable to byte, short
    "aByte ^= anInt", // ^= applicable to byte, int
    "aByte ^= aLong", // ^= applicable to byte, long
    "aShort ^= aChar", // ^= applicable to short, char
    "aShort ^= aByte", // ^= applicable to short, byte
    "aShort ^= aShort", // ^= applicable to short, short
    "aShort ^= anInt", // ^= applicable to short, int
    "aShort ^= aLong", // ^= applicable to short, long
    "anInt ^= aChar", // ^= applicable to int, char
    "anInt ^= aByte", // ^= applicable to int, byte
    "anInt ^= aShort", // ^= applicable to int, short
    "anInt ^= anInt", // ^= applicable to int, int
    "anInt ^= aLong", // ^= applicable to int, long
    "aLong ^= aChar", // ^= applicable to long, char
    "aLong ^= aByte", // ^= applicable to long, byte
    "aLong ^= aShort", // ^= applicable to long, short
    "aLong ^= anInt", // ^= applicable to long, int
    "aLong ^= aLong", // ^= applicable to long, long
    "++aChar", // ++ applicable to char
    "++aByte", // ++ applicable to byte
    "++aShort", // ++ applicable to short
    "++anInt", // ++ applicable to int
    "++aLong", // ++ applicable to long
    "++aFloat", // ++ applicable to float
    "++aDouble", // ++ applicable to double
    "--aChar", // -- applicable to char
    "--aByte", // -- applicable to byte
    "--aShort", // -- applicable to short
    "--anInt", // -- applicable to int
    "--aLong", // -- applicable to long
    "--aFloat", // -- applicable to float
    "--aDouble", // -- applicable to double
    "aChar++", // ++ applicable to char
    "aByte++", // ++ applicable to byte
    "aShort++", // ++ applicable to short
    "anInt++", // ++ applicable to int
    "aLong++", // ++ applicable to long
    "aFloat++", // ++ applicable to float
    "aDouble++", // ++ applicable to double
    "aChar--", // -- applicable to char
    "aByte--", // -- applicable to byte
    "aShort--", // -- applicable to short
    "anInt--", // -- applicable to int
    "aLong--", // -- applicable to long
    "aFloat--", // -- applicable to float
    "aDouble--", // -- applicable to double
    "aChar = ++aChar", // ++ applicable to char, result is char
    "aByte = ++aByte", // ++ applicable to byte, result is byte
    "aShort = ++aShort", // ++ applicable to short, result is short
    "anInt = ++anInt", // ++ applicable to int, result is int
    "aLong = ++aLong", // ++ applicable to long, result is long
    "aFloat = ++aFloat", // ++ applicable to float, result is float
    "aDouble = ++aDouble", // ++ applicable to double, result is double
    "aChar = --aChar", // -- applicable to char, result is char
    "aByte = --aByte", // -- applicable to byte, result is byte
    "aShort = --aShort", // -- applicable to short, result is short
    "anInt = --anInt", // -- applicable to int, result is int
    "aLong = --aLong", // -- applicable to long, result is long
    "aFloat = --aFloat", // -- applicable to float, result is float
    "aDouble = --aDouble", // -- applicable to double, result is double
    "aChar = aChar++", // ++ applicable to char, result is char
    "aByte = aByte++", // ++ applicable to byte, result is byte
    "aShort = aShort++", // ++ applicable to short, result is short
    "anInt = anInt++", // ++ applicable to int, result is int
    "aLong = aLong++", // ++ applicable to long, result is long
    "aFloat = aFloat++", // ++ applicable to float, result is float
    "aDouble = aDouble++", // ++ applicable to double, result is double
    "aChar = aChar--", // -- applicable to char, result is char
    "aByte = aByte--", // -- applicable to byte, result is byte
    "aShort = aShort--", // -- applicable to short, result is short
    "anInt = anInt--", // -- applicable to int, result is int
    "aLong = aLong--", // -- applicable to long, result is long
    "aFloat = aFloat--", // -- applicable to float, result is float
    "aDouble = aDouble--", // -- applicable to double, result is double
    "~aChar", // ~ applicable to char
    "~aByte", // ~ applicable to byte
    "~aShort", // ~ applicable to short
    "~anInt", // ~ applicable to int
    "~aLong", // ~ applicable to long
    "!aBoolean", // ~ applicable to boolean
    "anInt = ~aChar", // ~ applicable to char, result is int
    "anInt = ~aByte", // ~ applicable to byte, result is int
    "anInt = ~aShort", // ~ applicable to short, result is int
    "anInt = ~anInt", // ~ applicable to int, result is int
    "aLong = ~aLong", // ~ applicable to long, result is long
    "aBoolean = !aBoolean", // ~ applicable to boolean, result is boolean
    // "aChar = 0 + 0", // expected char and provided char (int conversion)
    // "aChar = 65535 + 0", // expected char and provided char (int conversion)
    // "aChar = 0 + 65535", // expected char and provided char (int conversion)
    // "aChar = 65536 - 1", // expected char and provided char (int conversion)
    // "aChar = -1 + 65536", // expected char and provided char (int conversion)
    // "aChar = 65535 + 1 - 1", // expected char and provided char (int conversion)
    // "aChar = 0 * 0", // expected char and provided char (int conversion)
    // "aChar = 0 * -1", // expected char and provided char (int conversion)
    // "aChar = -1 * 0", // expected char and provided char (int conversion)
    // "aChar = 65535 * 1", // expected char and provided char (int conversion)
    // "aChar = 1 * 65535", // expected char and provided char (int conversion)
    // "aChar = 32767 * 2 + 1", // expected char and provided char (int conversion)
    // "aChar = 32768 * 2 - 1", // expected char and provided char (int conversion)
    // "aChar = 65535 / 1", // expected char and provided char (int conversion)
    // "aChar = 1 / 65536", // expected char and provided char (int conversion)
    // "aChar = 65535 % 1", // expected char and provided char (int conversion)
    // "aChar = 1 % 65536", // expected char and provided char (int conversion)
    // "aByte = 0 + 0", // expected byte and provided byte (int conversion)
    // "aByte = 127 + 0", // expected byte and provided byte (int conversion)
    // "aByte = 0 + 127", // expected byte and provided byte (int conversion)
    // "aByte = -128 + 0", // expected byte and provided byte (int conversion)
    // "aByte = 0 + -128", // expected byte and provided byte (int conversion)
    // "aByte = 0 - 128", // expected byte and provided byte (int conversion)
    // "aByte = 128 - 1", // expected byte and provided byte (int conversion)
    // "aByte = 128 + -1", // expected byte and provided byte (int conversion)
    // "aByte = -1 + 128", // expected byte and provided byte (int conversion)
    // "aByte = -129 + 1", // expected byte and provided byte (int conversion)
    // "aByte = 1 + -129", // expected byte and provided byte (int conversion)
    // "aByte = 1 - 129", // expected byte and provided byte (int conversion)
    // "aByte = 127 + 1 - 1", // expected byte and provided byte (int conversion)
    // "aByte = -128 + 1 - 1", // expected byte and provided byte (int conversion)
    // "aByte = 0 * 0", // expected byte and provided byte (int conversion)
    // "aByte = 127 * 1", // expected byte and provided byte (int conversion)
    // "aByte = 1 * 127", // expected byte and provided byte (int conversion)
    // "aByte = 128 * -1", // expected byte and provided byte (int conversion)
    // "aByte = -1 * 128", // expected byte and provided byte (int conversion)
    // "aByte = 63 * 2 + 1", // expected byte and provided byte (int conversion)
    // "aByte = 64 * 2 - 1", // expected byte and provided byte (int conversion)
    // "aByte = 64 * -2", // expected byte and provided byte (int conversion)
    // "aByte = 127 / 1", // expected byte and provided byte (int conversion)
    // "aByte = 128 / -1", // expected byte and provided byte (int conversion)
    // "aByte = 1 / 128", // expected byte and provided byte (int conversion)
    // "aByte = 127 % 1", // expected byte and provided byte (int conversion)
    // "aByte = -128 % 1", // expected byte and provided byte (int conversion)
    // "aByte = 1 % 128", // expected byte and provided byte (int conversion)
    // "aShort = 0 + 0", // expected short and provided short (int conversion)
    // "aShort = 32767 + 0", // expected short and provided short (int conversion)
    // "aShort = 0 + 32767", // expected short and provided short (int conversion)
    // "aShort = -32768 + 0", // expected short and provided short (int conversion)
    // "aShort = 0 + -32768", // expected short and provided short (int conversion)
    // "aShort = 0 - 32768", // expected short and provided short (int conversion)
    // "aShort = 32768 - 1", // expected short and provided short (int conversion)
    // "aShort = 32768 + -1", // expected short and provided short (int conversion)
    // "aShort = -1 + 32768", // expected short and provided short (int conversion)
    // "aShort = -32769 + 1", // expected short and provided short (int conversion)
    // "aShort = 1 + -32769", // expected short and provided short (int conversion)
    // "aShort = 1 - 32769", // expected short and provided short (int conversion)
    // "aShort = 32767 + 1 - 1", // expected short and provided short (int conversion)
    // "aShort = -32768 + 1 - 1", // expected short and provided short (int conversion)
    // "aShort = 0 * 0", // expected short and provided short (int conversion)
    // "aShort = 32767 * 1", // expected short and provided short (int conversion)
    // "aShort = 1 * 32767", // expected short and provided short (int conversion)
    // "aShort = 32768 * -1", // expected short and provided short (int conversion)
    // "aShort = -1 * 32768", // expected short and provided short (int conversion)
    // "aShort = 16383 * 2 + 1", // expected short and provided short (int conversion)
    // "aShort = 16384 * 2 - 1", // expected short and provided short (int conversion)
    // "aShort = 16384 * -2", // expected short and provided short (int conversion)
    // "aShort = 32767 / 1", // expected short and provided short (int conversion)
    // "aShort = 32768 / -1", // expected short and provided short (int conversion)
    // "aShort = 1 / 32768", // expected short and provided short (int conversion)
    // "aShort = 32767 % 1", // expected short and provided short (int conversion)
    // "aShort = -32768 % 1", // expected short and provided short (int conversion)
    // "aShort = 1 % 32768", // expected short and provided short (int conversion)
    "anInt = 0 + 0", // expected int and provided int
    "anInt = 1 + -1", // expected int and provided int
    "anInt = -1 + 1", // expected int and provided int
    "anInt = 1 - 1", // expected int and provided int
    "anInt = 127 + 1", // expected int and provided int
    "anInt = -128 - 1", // expected int and provided int
    "anInt = 32767 + 1", // expected int and provided int
    "anInt = -32768 - 1", // expected int and provided int
    "anInt = 65536 + 1", // expected int and provided int
    "anInt = 2147483647 + 0", // expected int and provided int
    "anInt = -2147483648 - 0", // expected int and provided int
    "anInt = 2147483647 + 1 - 1", // expected int and provided int
    "anInt = -2147483648 - 1 + 1", // expected int and provided int
    "anInt = 0 * 0", // expected int and provided int
    "anInt = 2147483647 * 1", // expected int and provided int
    "anInt = 1 * 2147483647", // expected int and provided int
    "anInt = -2147483648 * 1", // expected int and provided int
    "anInt = 1 * -2147483648", // expected int and provided int
    "anInt = 1073741823 * 2 + 1", // expected int and provided int
    "anInt = 1073741823 * 2 - 1", // expected int and provided int
    "anInt = 1073741824 * -2", // expected int and provided int
    "anInt = 2147483647 / 1", // expected int and provided int
    "anInt = -2147483648 / 1", // expected int and provided int
    "anInt = 1 / 2147483647", // expected int and provided int
    "anInt = 2147483647 % 1", // expected int and provided int
    "anInt = -2147483648 % 1", // expected int and provided int
    "anInt = 1 % 2147483647", // expected int and provided int
    "anInt = aChar + aChar", // + applicable to char, char, result is int
    "anInt = aChar - aChar", // - applicable to char, char, result is int
    "anInt = aChar * aChar", // * applicable to char, char, result is int
    "anInt = aChar / aChar", // / applicable to char, char, result is int
    "anInt = aChar % aChar", // % applicable to char, char, result is int
    "anInt = aByte + aByte", // + applicable to byte, byte, result is int
    "anInt = aByte - aByte", // - applicable to byte, byte, result is int
    "anInt = aByte * aByte", // * applicable to byte, byte, result is int
    "anInt = aByte / aByte", // / applicable to byte, byte, result is int
    "anInt = aByte % aByte", // % applicable to byte, byte, result is int
    "anInt = aShort + aShort", // + applicable to short, short, result is int
    "anInt = aShort - aShort", // - applicable to short, short, result is int
    "anInt = aShort * aShort", // * applicable to short, short, result is int
    "anInt = aShort / aShort", // / applicable to short, short, result is int
    "anInt = aShort % aShort", // % applicable to short, short, result is int
    "anInt = anInt + anInt", // + applicable to int, int, result is int
    "anInt = anInt - anInt", // - applicable to int, int, result is int
    "anInt = anInt * anInt", // * applicable to int, int, result is int
    "anInt = anInt / anInt", // / applicable to int, int, result is int
    "anInt = anInt % anInt", // % applicable to int, int, result is int
    "aLong = aLong + aLong", // + applicable to long, long, result is long
    "aLong = aLong - aLong", // - applicable to long, long, result is long
    "aLong = aLong * aLong", // * applicable to long, long, result is long
    "aLong = aLong / aLong", // / applicable to long, long, result is long
    "aLong = aLong % aLong", // % applicable to long, long, result is long
    "aFloat = aFloat + aFloat", // + applicable to float, float, result is float
    "aFloat = aFloat - aFloat", // - applicable to float, float, result is float
    "aFloat = aFloat * aFloat", // * applicable to float, float, result is float
    "aFloat = aFloat / aFloat", // / applicable to float, float, result is float
    "aFloat = aFloat % aFloat", // % applicable to float, float, result is float
    "aDouble = aDouble + aDouble", // + applicable to double, double, result is double
    "aDouble = aDouble - aDouble", // - applicable to double, double, result is double
    "aDouble = aDouble * aDouble", // * applicable to double, double, result is double
    "aDouble = aDouble / aDouble", // / applicable to double, double, result is double
    "aDouble = aDouble % aDouble", // % applicable to double, double, result is double
    "aBoolean = aBoolean && aBoolean", // && applicable to boolean, boolean, result is boolean
    "aBoolean = aBoolean || aBoolean", // || applicable to boolean, boolean, result is boolean
    "aBoolean = aByte > aByte", // > applicable to byte, byte, result is boolean
    "aBoolean = aByte > aShort", // > applicable to byte, short, result is boolean
    "aBoolean = aByte > aChar", // > applicable to byte, char, result is boolean
    "aBoolean = aByte > anInt", // > applicable to byte, int, result is boolean
    "aBoolean = aByte > aLong", // > applicable to byte, long, result is boolean
    "aBoolean = aByte > aFloat", // > applicable to byte, float, result is boolean
    "aBoolean = aByte > aDouble", // > applicable to byte, double, result is boolean
    "aBoolean = aShort > aByte", // > applicable to short, byte, result is boolean
    "aBoolean = aShort > aShort", // > applicable to short, short, result is boolean
    "aBoolean = aShort > aChar", // > applicable to short, char, result is boolean
    "aBoolean = aShort > anInt", // > applicable to short, int, result is boolean
    "aBoolean = aShort > aLong", // > applicable to short, long, result is boolean
    "aBoolean = aShort > aFloat", // > applicable to short, float, result is boolean
    "aBoolean = aShort > aDouble", // > applicable to short, double, result is boolean
    "aBoolean = aChar > aByte", // > applicable to char, byte, result is boolean
    "aBoolean = aChar > aShort", // > applicable to char, short, result is boolean
    "aBoolean = aChar > aChar", // > applicable to char, char, result is boolean
    "aBoolean = aChar > anInt", // > applicable to char, int, result is boolean
    "aBoolean = aChar > aLong", // > applicable to char, long, result is boolean
    "aBoolean = aChar > aFloat", // > applicable to char, float, result is boolean
    "aBoolean = aChar > aDouble", // > applicable to char, double, result is boolean
    "aBoolean = anInt > aByte", // > applicable to int, byte, result is boolean
    "aBoolean = anInt > aShort", // > applicable to int, short, result is boolean
    "aBoolean = anInt > aChar", // > applicable to int, char, result is boolean
    "aBoolean = anInt > anInt", // > applicable to int, int, result is boolean
    "aBoolean = anInt > aLong", // > applicable to int, long, result is boolean
    "aBoolean = anInt > aFloat", // > applicable to int, float, result is boolean
    "aBoolean = anInt > aDouble", // > applicable to int, double, result is boolean
    "aBoolean = aLong > aByte", // > applicable to long, byte, result is boolean
    "aBoolean = aLong > aShort", // > applicable to long, short, result is boolean
    "aBoolean = aLong > aChar", // > applicable to long, char, result is boolean
    "aBoolean = aLong > anInt", // > applicable to long, int, result is boolean
    "aBoolean = aLong > aLong", // > applicable to long, long, result is boolean
    "aBoolean = aLong > aFloat", // > applicable to long, float, result is boolean
    "aBoolean = aLong > aDouble", // > applicable to long, double, result is boolean
    "aBoolean = aFloat > aByte", // > applicable to float, byte, result is boolean
    "aBoolean = aFloat > aShort", // > applicable to float, short, result is boolean
    "aBoolean = aFloat > aChar", // > applicable to float, char, result is boolean
    "aBoolean = aFloat > anInt", // > applicable to float, int, result is boolean
    "aBoolean = aFloat > aLong", // > applicable to float, long, result is boolean
    "aBoolean = aFloat > aFloat", // > applicable to float, float, result is boolean
    "aBoolean = aFloat > aDouble", // > applicable to float, double, result is boolean
    "aBoolean = aDouble > aByte", // > applicable to double, byte, result is boolean
    "aBoolean = aDouble > aShort", // > applicable to double, short, result is boolean
    "aBoolean = aDouble > aChar", // > applicable to double, char, result is boolean
    "aBoolean = aDouble > anInt", // > applicable to double, int, result is boolean
    "aBoolean = aDouble > aLong", // > applicable to double, long, result is boolean
    "aBoolean = aDouble > aFloat", // > applicable to double, float, result is boolean
    "aBoolean = aDouble > aDouble", // > applicable to double, double, result is boolean
    "aBoolean = aByte < aByte", // < applicable to byte, byte, result is boolean
    "aBoolean = aByte < aShort", // < applicable to byte, short, result is boolean
    "aBoolean = aByte < aChar", // < applicable to byte, char, result is boolean
    "aBoolean = aByte < anInt", // < applicable to byte, int, result is boolean
    "aBoolean = aByte < aLong", // < applicable to byte, long, result is boolean
    "aBoolean = aByte < aFloat", // < applicable to byte, float, result is boolean
    "aBoolean = aByte < aDouble", // < applicable to byte, double, result is boolean
    "aBoolean = aShort < aByte", // < applicable to short, byte, result is boolean
    "aBoolean = aShort < aShort", // < applicable to short, short, result is boolean
    "aBoolean = aShort < aChar", // < applicable to short, char, result is boolean
    "aBoolean = aShort < anInt", // < applicable to short, int, result is boolean
    "aBoolean = aShort < aLong", // < applicable to short, long, result is boolean
    "aBoolean = aShort < aFloat", // < applicable to short, float, result is boolean
    "aBoolean = aShort < aDouble", // < applicable to short, double, result is boolean
    "aBoolean = aChar < aByte", // < applicable to char, byte, result is boolean
    "aBoolean = aChar < aShort", // < applicable to char, short, result is boolean
    "aBoolean = aChar < aChar", // < applicable to char, char, result is boolean
    "aBoolean = aChar < anInt", // < applicable to char, int, result is boolean
    "aBoolean = aChar < aLong", // < applicable to char, long, result is boolean
    "aBoolean = aChar < aFloat", // < applicable to char, float, result is boolean
    "aBoolean = aChar < aDouble", // < applicable to char, double, result is boolean
    "aBoolean = anInt < aByte", // < applicable to int, byte, result is boolean
    "aBoolean = anInt < aShort", // < applicable to int, short, result is boolean
    "aBoolean = anInt < aChar", // < applicable to int, char, result is boolean
    "aBoolean = anInt < anInt", // < applicable to int, int, result is boolean
    "aBoolean = anInt < aLong", // < applicable to int, long, result is boolean
    "aBoolean = anInt < aFloat", // < applicable to int, float, result is boolean
    "aBoolean = anInt < aDouble", // < applicable to int, double, result is boolean
    "aBoolean = aLong < aByte", // < applicable to long, byte, result is boolean
    "aBoolean = aLong < aShort", // < applicable to long, short, result is boolean
    "aBoolean = aLong < aChar", // < applicable to long, char, result is boolean
    "aBoolean = aLong < anInt", // < applicable to long, int, result is boolean
    "aBoolean = aLong < aLong", // < applicable to long, long, result is boolean
    "aBoolean = aLong < aFloat", // < applicable to long, float, result is boolean
    "aBoolean = aLong < aDouble", // < applicable to long, double, result is boolean
    "aBoolean = aFloat < aByte", // < applicable to float, byte, result is boolean
    "aBoolean = aFloat < aShort", // < applicable to float, short, result is boolean
    "aBoolean = aFloat < aChar", // < applicable to float, char, result is boolean
    "aBoolean = aFloat < anInt", // < applicable to float, int, result is boolean
    "aBoolean = aFloat < aLong", // < applicable to float, long, result is boolean
    "aBoolean = aFloat < aFloat", // < applicable to float, float, result is boolean
    "aBoolean = aFloat < aDouble", // < applicable to float, double, result is boolean
    "aBoolean = aDouble < aByte", // < applicable to double, byte, result is boolean
    "aBoolean = aDouble < aShort", // < applicable to double, short, result is boolean
    "aBoolean = aDouble < aChar", // < applicable to double, char, result is boolean
    "aBoolean = aDouble < anInt", // < applicable to double, int, result is boolean
    "aBoolean = aDouble < aLong", // < applicable to double, long, result is boolean
    "aBoolean = aDouble < aFloat", // < applicable to double, float, result is boolean
    "aBoolean = aDouble < aDouble", // < applicable to double, double, result is boolean
    "aBoolean = aByte >= aByte", // >= applicable to byte, byte, result is boolean
    "aBoolean = aByte >= aShort", // >= applicable to byte, short, result is boolean
    "aBoolean = aByte >= aChar", // >= applicable to byte, char, result is boolean
    "aBoolean = aByte >= anInt", // >= applicable to byte, int, result is boolean
    "aBoolean = aByte >= aLong", // >= applicable to byte, long, result is boolean
    "aBoolean = aByte >= aFloat", // >= applicable to byte, float, result is boolean
    "aBoolean = aByte >= aDouble", // >= applicable to byte, double, result is boolean
    "aBoolean = aShort >= aByte", // >= applicable to short, byte, result is boolean
    "aBoolean = aShort >= aShort", // >= applicable to short, short, result is boolean
    "aBoolean = aShort >= aChar", // >= applicable to short, char, result is boolean
    "aBoolean = aShort >= anInt", // >= applicable to short, int, result is boolean
    "aBoolean = aShort >= aLong", // >= applicable to short, long, result is boolean
    "aBoolean = aShort >= aFloat", // >= applicable to short, float, result is boolean
    "aBoolean = aShort >= aDouble", // >= applicable to short, double, result is boolean
    "aBoolean = aChar >= aByte", // >= applicable to char, byte, result is boolean
    "aBoolean = aChar >= aShort", // >= applicable to char, short, result is boolean
    "aBoolean = aChar >= aChar", // >= applicable to char, char, result is boolean
    "aBoolean = aChar >= anInt", // >= applicable to char, int, result is boolean
    "aBoolean = aChar >= aLong", // >= applicable to char, long, result is boolean
    "aBoolean = aChar >= aFloat", // >= applicable to char, float, result is boolean
    "aBoolean = aChar >= aDouble", // >= applicable to char, double, result is boolean
    "aBoolean = anInt >= aByte", // >= applicable to int, byte, result is boolean
    "aBoolean = anInt >= aShort", // >= applicable to int, short, result is boolean
    "aBoolean = anInt >= aChar", // >= applicable to int, char, result is boolean
    "aBoolean = anInt >= anInt", // >= applicable to int, int, result is boolean
    "aBoolean = anInt >= aLong", // >= applicable to int, long, result is boolean
    "aBoolean = anInt >= aFloat", // >= applicable to int, float, result is boolean
    "aBoolean = anInt >= aDouble", // >= applicable to int, double, result is boolean
    "aBoolean = aLong >= aByte", // >= applicable to long, byte, result is boolean
    "aBoolean = aLong >= aShort", // >= applicable to long, short, result is boolean
    "aBoolean = aLong >= aChar", // >= applicable to long, char, result is boolean
    "aBoolean = aLong >= anInt", // >= applicable to long, int, result is boolean
    "aBoolean = aLong >= aLong", // >= applicable to long, long, result is boolean
    "aBoolean = aLong >= aFloat", // >= applicable to long, float, result is boolean
    "aBoolean = aLong >= aDouble", // >= applicable to long, double, result is boolean
    "aBoolean = aFloat >= aByte", // >= applicable to float, byte, result is boolean
    "aBoolean = aFloat >= aShort", // >= applicable to float, short, result is boolean
    "aBoolean = aFloat >= aChar", // >= applicable to float, char, result is boolean
    "aBoolean = aFloat >= anInt", // >= applicable to float, int, result is boolean
    "aBoolean = aFloat >= aLong", // >= applicable to float, long, result is boolean
    "aBoolean = aFloat >= aFloat", // >= applicable to float, float, result is boolean
    "aBoolean = aFloat >= aDouble", // >= applicable to float, double, result is boolean
    "aBoolean = aDouble >= aByte", // >= applicable to double, byte, result is boolean
    "aBoolean = aDouble >= aShort", // >= applicable to double, short, result is boolean
    "aBoolean = aDouble >= aChar", // >= applicable to double, char, result is boolean
    "aBoolean = aDouble >= anInt", // >= applicable to double, int, result is boolean
    "aBoolean = aDouble >= aLong", // >= applicable to double, long, result is boolean
    "aBoolean = aDouble >= aFloat", // >= applicable to double, float, result is boolean
    "aBoolean = aDouble >= aDouble", // >= applicable to double, double, result is boolean
    "aBoolean = aByte <= aByte", // <= applicable to byte, byte, result is boolean
    "aBoolean = aByte <= aShort", // <= applicable to byte, short, result is boolean
    "aBoolean = aByte <= aChar", // <= applicable to byte, char, result is boolean
    "aBoolean = aByte <= anInt", // <= applicable to byte, int, result is boolean
    "aBoolean = aByte <= aLong", // <= applicable to byte, long, result is boolean
    "aBoolean = aByte <= aFloat", // <= applicable to byte, float, result is boolean
    "aBoolean = aByte <= aDouble", // <= applicable to byte, double, result is boolean
    "aBoolean = aShort <= aByte", // <= applicable to short, byte, result is boolean
    "aBoolean = aShort <= aShort", // <= applicable to short, short, result is boolean
    "aBoolean = aShort <= aChar", // <= applicable to short, char, result is boolean
    "aBoolean = aShort <= anInt", // <= applicable to short, int, result is boolean
    "aBoolean = aShort <= aLong", // <= applicable to short, long, result is boolean
    "aBoolean = aShort <= aFloat", // <= applicable to short, float, result is boolean
    "aBoolean = aShort <= aDouble", // <= applicable to short, double, result is boolean
    "aBoolean = aChar <= aByte", // <= applicable to char, byte, result is boolean
    "aBoolean = aChar <= aShort", // <= applicable to char, short, result is boolean
    "aBoolean = aChar <= aChar", // <= applicable to char, char, result is boolean
    "aBoolean = aChar <= anInt", // <= applicable to char, int, result is boolean
    "aBoolean = aChar <= aLong", // <= applicable to char, long, result is boolean
    "aBoolean = aChar <= aFloat", // <= applicable to char, float, result is boolean
    "aBoolean = aChar <= aDouble", // <= applicable to char, double, result is boolean
    "aBoolean = anInt <= aByte", // <= applicable to int, byte, result is boolean
    "aBoolean = anInt <= aShort", // <= applicable to int, short, result is boolean
    "aBoolean = anInt <= aChar", // <= applicable to int, char, result is boolean
    "aBoolean = anInt <= anInt", // <= applicable to int, int, result is boolean
    "aBoolean = anInt <= aLong", // <= applicable to int, long, result is boolean
    "aBoolean = anInt <= aFloat", // <= applicable to int, float, result is boolean
    "aBoolean = anInt <= aDouble", // <= applicable to int, double, result is boolean
    "aBoolean = aLong <= aByte", // <= applicable to long, byte, result is boolean
    "aBoolean = aLong <= aShort", // <= applicable to long, short, result is boolean
    "aBoolean = aLong <= aChar", // <= applicable to long, char, result is boolean
    "aBoolean = aLong <= anInt", // <= applicable to long, int, result is boolean
    "aBoolean = aLong <= aLong", // <= applicable to long, long, result is boolean
    "aBoolean = aLong <= aFloat", // <= applicable to long, float, result is boolean
    "aBoolean = aLong <= aDouble", // <= applicable to long, double, result is boolean
    "aBoolean = aFloat <= aByte", // <= applicable to float, byte, result is boolean
    "aBoolean = aFloat <= aShort", // <= applicable to float, short, result is boolean
    "aBoolean = aFloat <= aChar", // <= applicable to float, char, result is boolean
    "aBoolean = aFloat <= anInt", // <= applicable to float, int, result is boolean
    "aBoolean = aFloat <= aLong", // <= applicable to float, long, result is boolean
    "aBoolean = aFloat <= aFloat", // <= applicable to float, float, result is boolean
    "aBoolean = aFloat <= aDouble", // <= applicable to float, double, result is boolean
    "aBoolean = aDouble <= aByte", // <= applicable to double, byte, result is boolean
    "aBoolean = aDouble <= aShort", // <= applicable to double, short, result is boolean
    "aBoolean = aDouble <= aChar", // <= applicable to double, char, result is boolean
    "aBoolean = aDouble <= anInt", // <= applicable to double, int, result is boolean
    "aBoolean = aDouble <= aLong", // <= applicable to double, long, result is boolean
    "aBoolean = aDouble <= aFloat", // <= applicable to double, float, result is boolean
    "aBoolean = aDouble <= aDouble", // <= applicable to double, double, result is boolean
    "aBoolean = aByte == aByte", // == applicable to byte, byte, result is boolean
    "aBoolean = aByte == aShort", // == applicable to byte, short, result is boolean
    "aBoolean = aByte == aChar", // == applicable to byte, char, result is boolean
    "aBoolean = aByte == anInt", // == applicable to byte, int, result is boolean
    "aBoolean = aByte == aLong", // == applicable to byte, long, result is boolean
    "aBoolean = aByte == aFloat", // == applicable to byte, float, result is boolean
    "aBoolean = aByte == aDouble", // == applicable to byte, double, result is boolean
    "aBoolean = aShort == aByte", // == applicable to short, byte, result is boolean
    "aBoolean = aShort == aShort", // == applicable to short, short, result is boolean
    "aBoolean = aShort == aChar", // == applicable to short, char, result is boolean
    "aBoolean = aShort == anInt", // == applicable to short, int, result is boolean
    "aBoolean = aShort == aLong", // == applicable to short, long, result is boolean
    "aBoolean = aShort == aFloat", // == applicable to short, float, result is boolean
    "aBoolean = aShort == aDouble", // == applicable to short, double, result is boolean
    "aBoolean = aChar == aByte", // == applicable to char, byte, result is boolean
    "aBoolean = aChar == aShort", // == applicable to char, short, result is boolean
    "aBoolean = aChar == aChar", // == applicable to char, char, result is boolean
    "aBoolean = aChar == anInt", // == applicable to char, int, result is boolean
    "aBoolean = aChar == aLong", // == applicable to char, long, result is boolean
    "aBoolean = aChar == aFloat", // == applicable to char, float, result is boolean
    "aBoolean = aChar == aDouble", // == applicable to char, double, result is boolean
    "aBoolean = anInt == aByte", // == applicable to int, byte, result is boolean
    "aBoolean = anInt == aShort", // == applicable to int, short, result is boolean
    "aBoolean = anInt == aChar", // == applicable to int, char, result is boolean
    "aBoolean = anInt == anInt", // == applicable to int, int, result is boolean
    "aBoolean = anInt == aLong", // == applicable to int, long, result is boolean
    "aBoolean = anInt == aFloat", // == applicable to int, float, result is boolean
    "aBoolean = anInt == aDouble", // == applicable to int, double, result is boolean
    "aBoolean = aLong == aByte", // == applicable to long, byte, result is boolean
    "aBoolean = aLong == aShort", // == applicable to long, short, result is boolean
    "aBoolean = aLong == aChar", // == applicable to long, char, result is boolean
    "aBoolean = aLong == anInt", // == applicable to long, int, result is boolean
    "aBoolean = aLong == aLong", // == applicable to long, long, result is boolean
    "aBoolean = aLong == aFloat", // == applicable to long, float, result is boolean
    "aBoolean = aLong == aDouble", // == applicable to long, double, result is boolean
    "aBoolean = aFloat == aByte", // == applicable to float, byte, result is boolean
    "aBoolean = aFloat == aShort", // == applicable to float, short, result is boolean
    "aBoolean = aFloat == aChar", // == applicable to float, char, result is boolean
    "aBoolean = aFloat == anInt", // == applicable to float, int, result is boolean
    "aBoolean = aFloat == aLong", // == applicable to float, long, result is boolean
    "aBoolean = aFloat == aFloat", // == applicable to float, float, result is boolean
    "aBoolean = aFloat == aDouble", // == applicable to float, double, result is boolean
    "aBoolean = aDouble == aByte", // == applicable to double, byte, result is boolean
    "aBoolean = aDouble == aShort", // == applicable to double, short, result is boolean
    "aBoolean = aDouble == aChar", // == applicable to double, char, result is boolean
    "aBoolean = aDouble == anInt", // == applicable to double, int, result is boolean
    "aBoolean = aDouble == aLong", // == applicable to double, long, result is boolean
    "aBoolean = aDouble == aFloat", // == applicable to double, float, result is boolean
    "aBoolean = aDouble == aDouble", // == applicable to double, double, result is boolean
    "aBoolean = aByte != aByte", // != applicable to byte, byte, result is boolean
    "aBoolean = aByte != aShort", // != applicable to byte, short, result is boolean
    "aBoolean = aByte != aChar", // != applicable to byte, char, result is boolean
    "aBoolean = aByte != anInt", // != applicable to byte, int, result is boolean
    "aBoolean = aByte != aLong", // != applicable to byte, long, result is boolean
    "aBoolean = aByte != aFloat", // != applicable to byte, float, result is boolean
    "aBoolean = aByte != aDouble", // != applicable to byte, double, result is boolean
    "aBoolean = aShort != aByte", // != applicable to short, byte, result is boolean
    "aBoolean = aShort != aShort", // != applicable to short, short, result is boolean
    "aBoolean = aShort != aChar", // != applicable to short, char, result is boolean
    "aBoolean = aShort != anInt", // != applicable to short, int, result is boolean
    "aBoolean = aShort != aLong", // != applicable to short, long, result is boolean
    "aBoolean = aShort != aFloat", // != applicable to short, float, result is boolean
    "aBoolean = aShort != aDouble", // != applicable to short, double, result is boolean
    "aBoolean = aChar != aByte", // != applicable to char, byte, result is boolean
    "aBoolean = aChar != aShort", // != applicable to char, short, result is boolean
    "aBoolean = aChar != aChar", // != applicable to char, char, result is boolean
    "aBoolean = aChar != anInt", // != applicable to char, int, result is boolean
    "aBoolean = aChar != aLong", // != applicable to char, long, result is boolean
    "aBoolean = aChar != aFloat", // != applicable to char, float, result is boolean
    "aBoolean = aChar != aDouble", // != applicable to char, double, result is boolean
    "aBoolean = anInt != aByte", // != applicable to int, byte, result is boolean
    "aBoolean = anInt != aShort", // != applicable to int, short, result is boolean
    "aBoolean = anInt != aChar", // != applicable to int, char, result is boolean
    "aBoolean = anInt != anInt", // != applicable to int, int, result is boolean
    "aBoolean = anInt != aLong", // != applicable to int, long, result is boolean
    "aBoolean = anInt != aFloat", // != applicable to int, float, result is boolean
    "aBoolean = anInt != aDouble", // != applicable to int, double, result is boolean
    "aBoolean = aLong != aByte", // != applicable to long, byte, result is boolean
    "aBoolean = aLong != aShort", // != applicable to long, short, result is boolean
    "aBoolean = aLong != aChar", // != applicable to long, char, result is boolean
    "aBoolean = aLong != anInt", // != applicable to long, int, result is boolean
    "aBoolean = aLong != aLong", // != applicable to long, long, result is boolean
    "aBoolean = aLong != aFloat", // != applicable to long, float, result is boolean
    "aBoolean = aLong != aDouble", // != applicable to long, double, result is boolean
    "aBoolean = aFloat != aByte", // != applicable to float, byte, result is boolean
    "aBoolean = aFloat != aShort", // != applicable to float, short, result is boolean
    "aBoolean = aFloat != aChar", // != applicable to float, char, result is boolean
    "aBoolean = aFloat != anInt", // != applicable to float, int, result is boolean
    "aBoolean = aFloat != aLong", // != applicable to float, long, result is boolean
    "aBoolean = aFloat != aFloat", // != applicable to float, float, result is boolean
    "aBoolean = aFloat != aDouble", // != applicable to float, double, result is boolean
    "aBoolean = aDouble != aByte", // != applicable to double, byte, result is boolean
    "aBoolean = aDouble != aShort", // != applicable to double, short, result is boolean
    "aBoolean = aDouble != aChar", // != applicable to double, char, result is boolean
    "aBoolean = aDouble != anInt", // != applicable to double, int, result is boolean
    "aBoolean = aDouble != aLong", // != applicable to double, long, result is boolean
    "aBoolean = aDouble != aFloat", // != applicable to double, float, result is boolean
    "aBoolean = aDouble != aDouble", // != applicable to double, double, result is boolean
    "aBoolean ? 0 : 1", // ? applicable to boolean
    "aBoolean = aBoolean ? aBoolean : aBoolean", // ? applicable to boolean, boolean, result is boolean
    "aByte = aBoolean ? aByte : aByte", // ? applicable to byte, byte, result is byte
    "aShort = aBoolean ? aByte : aShort", // ? applicable to byte, short, result is short
    "aShort = aBoolean ? aShort : aByte", // ? applicable to short, byte, result is short
    "aShort = aBoolean ? aShort : aShort", // ? applicable to short, short, result is short
    "aChar = aBoolean ? aChar : aChar", // ? applicable to char, char, result is char
    "anInt = aBoolean ? aChar : aByte", // ? applicable to char, byte, result is int
    "anInt = aBoolean ? aByte : aChar", // ? applicable to byte, char, result is int
    "anInt = aBoolean ? aChar : aShort", // ? applicable to char, short, result is int
    "anInt = aBoolean ? aShort : aChar", // ? applicable to short, char, result is int
    "anInt = aBoolean ? anInt : aByte", // ? applicable to int, byte, result is int
    "anInt = aBoolean ? anInt : aShort", // ? applicable to int, short, result is int
    "anInt = aBoolean ? anInt : aChar", // ? applicable to int, char, result is int
    "anInt = aBoolean ? aByte : anInt", // ? applicable to byte, int, result is int
    "anInt = aBoolean ? aShort : anInt", // ? applicable to short, int, result is int
    "anInt = aBoolean ? aChar : anInt", // ? applicable to char, int, result is int
    "anInt = aBoolean ? anInt : anInt", // ? applicable to int, int, result is int
    "aLong = aBoolean ? aLong : aByte", // ? applicable to long, byte, result is long
    "aLong = aBoolean ? aLong : aShort", // ? applicable to long, short, result is long
    "aLong = aBoolean ? aLong : aChar", // ? applicable to long, char, result is long
    "aLong = aBoolean ? aLong : anInt", // ? applicable to long, int, result is long
    "aLong = aBoolean ? aByte : aLong", // ? applicable to byte, long, result is long
    "aLong = aBoolean ? aShort : aLong", // ? applicable to short, long, result is long
    "aLong = aBoolean ? aChar : aLong", // ? applicable to char, long, result is long
    "aLong = aBoolean ? anInt : aLong", // ? applicable to int, long, result is long
    "aLong = aBoolean ? aLong : aLong", // ? applicable to long, long, result is long
    "aFloat = aBoolean ? aFloat : aByte", // ? applicable to float, byte, result is float
    "aFloat = aBoolean ? aFloat : aShort", // ? applicable to float, short, result is float
    "aFloat = aBoolean ? aFloat : aChar", // ? applicable to float, char, result is float
    "aFloat = aBoolean ? aFloat : anInt", // ? applicable to float, int, result is float
    "aFloat = aBoolean ? aFloat : aLong", // ? applicable to float, long, result is float
    "aFloat = aBoolean ? aByte : aFloat", // ? applicable to byte, float, result is float
    "aFloat = aBoolean ? aShort : aFloat", // ? applicable to short, float, result is float
    "aFloat = aBoolean ? aChar : aFloat", // ? applicable to char, float, result is float
    "aFloat = aBoolean ? anInt : aFloat", // ? applicable to int, float, result is float
    "aFloat = aBoolean ? aLong : aFloat", // ? applicable to long, float, result is float
    "aFloat = aBoolean ? aFloat : aFloat", // ? applicable to float, float, result is float
    "aDouble = aBoolean ? aDouble : aByte", // ? applicable to double, byte, result is double
    "aDouble = aBoolean ? aDouble : aShort", // ? applicable to double, short, result is double
    "aDouble = aBoolean ? aDouble : aChar", // ? applicable to double, char, result is double
    "aDouble = aBoolean ? aDouble : anInt", // ? applicable to double, int, result is double
    "aDouble = aBoolean ? aDouble : aLong", // ? applicable to double, long, result is double
    "aDouble = aBoolean ? aDouble : aFloat", // ? applicable to double, long, result is double
    "aDouble = aBoolean ? aByte : aDouble", // ? applicable to byte, double, result is double
    "aDouble = aBoolean ? aShort : aDouble", // ? applicable to short, double, result is double
    "aDouble = aBoolean ? aChar : aDouble", // ? applicable to char, double, result is double
    "aDouble = aBoolean ? anInt : aDouble", // ? applicable to int, double, result is double
    "aDouble = aBoolean ? aLong : aDouble", // ? applicable to long, double, result is double
    "aDouble = aBoolean ? aFloat : aDouble", // ? applicable to float, double, result is double
    "aDouble = aBoolean ? aDouble : aDouble", // ? applicable to double, double, result is double
    "anInt = 1 << 2", // expected int provided int
    "anInt = 1 >> 2", // expected int provided int
    "anInt = 1 >>> 2", // expected int provided int
    "anInt = 1 & 2", // expected int provided int
    "anInt = 1 ^ 2", // expected int provided int
    "anInt = 1 | 2", // expected int provided int
    "aBoolean = true & true", // expected boolean provided boolean
    "aBoolean = true ^ true", // expected boolean provided boolean
    "aBoolean = true | true", // expected boolean provided boolean
  })
  public void testValidExpression(@NotNull String expr) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkArgument(!expr.isBlank());

    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    TypeCheck3.typeOf(ast);

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "aBoolean = 'a', EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided char
    "aBoolean = 0, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided int
    "aBoolean = 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided int
    "aBoolean = -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided int
    "aBoolean = 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided long
    "aBoolean = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided float
    "aBoolean = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided double
    "aBoolean = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided char
    "aBoolean = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided byte
    "aBoolean = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided short
    "aBoolean = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided int
    "aBoolean = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided long
    "aBoolean = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided float
    "aBoolean = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided double
    "aChar = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aChar = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aChar = -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 65536, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aChar = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided byte
    "aChar = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided short
    "aChar = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aByte = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aByte = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aByte = 128, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = -129, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aByte = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided char
    "aByte = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided short
    "aByte = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aShort = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "aShort = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "aShort = 32768, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = -32769, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "aShort = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided char
    "aShort = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "anInt = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "anInt = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    // "anInt = 2147483648, 0x12345", // integer literal too large
    // "anInt = -2147483649, 0x12345", // integer literal too large
    "anInt = 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "anInt = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "aLong = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aLong = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    // "aLong = 9223372036854775808l, 0x12345", // long literal too large
    // "aLong = -9223372036854775809l, 0x12345", // long literal too large
    "aLong = 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aLong = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aFloat = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aFloat = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aFloat = 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aFloat = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aDouble = true, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aDouble = false, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aDouble = aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aBoolean += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, boolean
    "aBoolean += aChar, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, char
    "aBoolean += aByte, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, byte
    "aBoolean += aShort, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, short
    "aBoolean += anInt, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, int
    "aBoolean += aLong, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, long
    "aBoolean += aFloat, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, float
    "aBoolean += aDouble, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to boolean, double
    "aChar += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to char, boolean
    "aByte += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to byte, boolean
    "aShort += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to short, boolean
    "anInt += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to int, boolean
    "aLong += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to long, boolean
    "aFloat += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to float, boolean
    "aDouble += aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // += not applicable to double, boolean
    "aBoolean -= aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, boolean
    "aBoolean -= aChar, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, char
    "aBoolean -= aByte, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, byte
    "aBoolean -= aShort, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, short
    "aBoolean -= anInt, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, int
    "aBoolean -= aLong, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, long
    "aBoolean -= aFloat, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, float
    "aBoolean -= aDouble, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // -= not applicable to boolean, double
    "aBoolean *= aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, boolean
    "aBoolean *= aChar, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, char
    "aBoolean *= aByte, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, byte
    "aBoolean *= aShort, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, short
    "aBoolean *= anInt, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, int
    "aBoolean *= aLong, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, long
    "aBoolean *= aFloat, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, float
    "aBoolean *= aDouble, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // *= not applicable to boolean, double
    "aBoolean /= aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, boolean
    "aBoolean /= aChar, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, char
    "aBoolean /= aByte, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, byte
    "aBoolean /= aShort, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, short
    "aBoolean /= anInt, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, int
    "aBoolean /= aLong, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, long
    "aBoolean /= aFloat, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, float
    "aBoolean /= aDouble, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // /= not applicable to boolean, double
    "aBoolean %= aBoolean, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, boolean
    "aBoolean %= aChar, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, char
    "aBoolean %= aByte, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, byte
    "aBoolean %= aShort, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, short
    "aBoolean %= anInt, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, int
    "aBoolean %= aLong, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, long
    "aBoolean %= aFloat, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, float
    "aBoolean %= aDouble, EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // %= not applicable to boolean, double
    "aBoolean >>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, boolean
    "aBoolean >>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, char
    "aBoolean >>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, byte
    "aBoolean >>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, short
    "aBoolean >>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, int
    "aBoolean >>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, long
    "aBoolean >>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, float
    "aBoolean >>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to boolean, double
    "aFloat >>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, boolean
    "aFloat >>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, char
    "aFloat >>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, byte
    "aFloat >>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, short
    "aFloat >>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, int
    "aFloat >>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, long
    "aFloat >>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, float
    "aFloat >>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to float, double
    "aDouble >>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, boolean
    "aDouble >>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, char
    "aDouble >>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, byte
    "aDouble >>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, short
    "aDouble >>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, int
    "aDouble >>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, long
    "aDouble >>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, float
    "aDouble >>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>= not applicable to double, double
    "aBoolean <<= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, boolean
    "aBoolean <<= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, char
    "aBoolean <<= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, byte
    "aBoolean <<= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, short
    "aBoolean <<= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, int
    "aBoolean <<= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, long
    "aBoolean <<= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, float
    "aBoolean <<= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to boolean, double
    "aFloat <<= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, boolean
    "aFloat <<= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, char
    "aFloat <<= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, byte
    "aFloat <<= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, short
    "aFloat <<= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, int
    "aFloat <<= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, long
    "aFloat <<= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, float
    "aFloat <<= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to float, double
    "aDouble <<= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, boolean
    "aDouble <<= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, char
    "aDouble <<= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, byte
    "aDouble <<= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, short
    "aDouble <<= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, int
    "aDouble <<= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, long
    "aDouble <<= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, float
    "aDouble <<= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // <<= not applicable to double, double
    "aBoolean >>>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, boolean
    "aBoolean >>>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, char
    "aBoolean >>>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, byte
    "aBoolean >>>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, short
    "aBoolean >>>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, int
    "aBoolean >>>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, long
    "aBoolean >>>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, float
    "aBoolean >>>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to boolean, double
    "aFloat >>>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, boolean
    "aFloat >>>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, char
    "aFloat >>>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, byte
    "aFloat >>>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, short
    "aFloat >>>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, int
    "aFloat >>>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, long
    "aFloat >>>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, float
    "aFloat >>>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to float, double
    "aDouble >>>= aBoolean, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, boolean
    "aDouble >>>= aChar, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, char
    "aDouble >>>= aByte, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, byte
    "aDouble >>>= aShort, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, short
    "aDouble >>>= anInt, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, int
    "aDouble >>>= aLong, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, long
    "aDouble >>>= aFloat, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, float
    "aDouble >>>= aDouble, EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // >>>= not applicable to double, double
    "aBoolean &= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, char
    "aBoolean &= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, byte
    "aBoolean &= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, short
    "aBoolean &= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, int
    "aBoolean &= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, long
    "aBoolean &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, float
    "aBoolean &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to boolean, double
    "aChar &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to char, boolean
    "aChar &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to char, float
    "aChar &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to char, double
    "aByte &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to byte, boolean
    "aByte &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to byte, float
    "aByte &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to byte, double
    "aShort &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to short, boolean
    "aShort &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to short, float
    "aShort &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to short, double
    "anInt &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to int, boolean
    "anInt &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to int, float
    "anInt &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to int, double
    "aLong &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to long, boolean
    "aLong &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to long, float
    "aLong &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to long, double
    "aFloat &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, boolean
    "aFloat &= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, char
    "aFloat &= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, byte
    "aFloat &= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, short
    "aFloat &= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, int
    "aFloat &= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, long
    "aFloat &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, float
    "aFloat &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to float, double
    "aDouble &= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, boolean
    "aDouble &= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, char
    "aDouble &= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, byte
    "aDouble &= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, short
    "aDouble &= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, int
    "aDouble &= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, long
    "aDouble &= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable to double, float
    "aDouble &= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // &= not applicable double, double
    "aBoolean |= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, char
    "aBoolean |= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, byte
    "aBoolean |= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, short
    "aBoolean |= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, int
    "aBoolean |= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, long
    "aBoolean |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, float
    "aBoolean |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to boolean, double
    "aChar |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to char, boolean
    "aChar |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to char, float
    "aChar |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to char, double
    "aByte |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to byte, boolean
    "aByte |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to byte, float
    "aByte |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to byte, double
    "aShort |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to short, boolean
    "aShort |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to short, float
    "aShort |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to short, double
    "anInt |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to int, boolean
    "anInt |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to int, float
    "anInt |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to int, double
    "aLong |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to long, boolean
    "aLong |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to long, float
    "aLong |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to long, double
    "aFloat |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, boolean
    "aFloat |= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, char
    "aFloat |= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, byte
    "aFloat |= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, short
    "aFloat |= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, int
    "aFloat |= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, long
    "aFloat |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, float
    "aFloat |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to float, double
    "aDouble |= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, boolean
    "aDouble |= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, char
    "aDouble |= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, byte
    "aDouble |= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, short
    "aDouble |= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, int
    "aDouble |= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, long
    "aDouble |= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable to double, float
    "aDouble |= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // |= not applicable double, double
    "aBoolean ^= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, char
    "aBoolean ^= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, byte
    "aBoolean ^= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, short
    "aBoolean ^= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, int
    "aBoolean ^= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, long
    "aBoolean ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, float
    "aBoolean ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to boolean, double
    "aChar ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to char, boolean
    "aChar ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to char, float
    "aChar ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to char, double
    "aByte ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to byte, boolean
    "aByte ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to byte, float
    "aByte ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to byte, double
    "aShort ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to short, boolean
    "aShort ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to short, float
    "aShort ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to short, double
    "anInt ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to int, boolean
    "anInt ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to int, float
    "anInt ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to int, double
    "aLong ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to long, boolean
    "aLong ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to long, float
    "aLong ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to long, double
    "aFloat ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, boolean
    "aFloat ^= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, char
    "aFloat ^= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, byte
    "aFloat ^= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, short
    "aFloat ^= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, int
    "aFloat ^= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, long
    "aFloat ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, float
    "aFloat ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to float, double
    "aDouble ^= aBoolean, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, boolean
    "aDouble ^= aChar, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, char
    "aDouble ^= aByte, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, byte
    "aDouble ^= aShort, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, short
    "aDouble ^= anInt, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, int
    "aDouble ^= aLong, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, long
    "aDouble ^= aFloat, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, float
    "aDouble ^= aDouble, EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE", // ^= not applicable to double, double
    "++aBoolean, EXPR_NUMERIC_AFFIX_OP_NOT_APPLICABLE", // ++ not applicable to boolean
    "--aBoolean, EXPR_NUMERIC_AFFIX_OP_NOT_APPLICABLE", // -- not applicable to boolean
    "aBoolean++, EXPR_NUMERIC_AFFIX_OP_NOT_APPLICABLE", // ++ not applicable to boolean
    "aBoolean--, EXPR_NUMERIC_AFFIX_OP_NOT_APPLICABLE", // -- not applicable to boolean
    "+aBoolean, EXPR_UNARY_OP_NOT_APPLICABLE", // + not applicable to boolean
    "-aBoolean, EXPR_UNARY_OP_NOT_APPLICABLE", // - not applicable to boolean
    "~aBoolean, EXPR_BITWISE_NOT_NOT_APPLICABLE", // ! not applicable to boolean
    "~aFloat, EXPR_BITWISE_NOT_NOT_APPLICABLE", // ! not applicable to boolean
    "~aDouble, EXPR_BITWISE_NOT_NOT_APPLICABLE", // ! not applicable to boolean
    "!aChar, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to char
    "!aByte, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to byte
    "!aShort, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to short
    "!anInt, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to int
    "!aLong, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to long
    "!aFloat, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to float
    "!aDouble, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to double
    "!aChar, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to char
    "!aByte, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to byte
    "!aShort, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to short
    "!anInt, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to int
    "!aLong, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to long
    "!aFloat, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to float
    "!aDouble, EXPR_LOGICAL_NOT_NOT_APPLICABLE", // ! not applicable to double
    "aChar = ~aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // ~ applicable to char, but result is int
    "aByte = ~aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // ~ applicable to byte, but result is int
    "aShort = ~aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // ~ applicable to short, but result is int
    "aBoolean + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, boolean
    "aBoolean + aChar, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, char
    "aBoolean + aByte, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, byte
    "aBoolean + aShort, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, short
    "aBoolean + anInt, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, int
    "aBoolean + aLong, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, long
    "aBoolean + aFloat, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, float
    "aBoolean + aDouble, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to boolean, double
    "aChar + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to char, boolean
    "aByte + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to byte, boolean
    "aShort + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to short, boolean
    "anInt + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to int, boolean
    "aLong + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to long, boolean
    "aFloat + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to float, boolean
    "aDouble + aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // + not applicable to double, boolean
    "aBoolean - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, boolean
    "aBoolean - aChar, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, char
    "aBoolean - aByte, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, byte
    "aBoolean - aShort, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, short
    "aBoolean - anInt, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, int
    "aBoolean - aLong, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, long
    "aBoolean - aFloat, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, float
    "aBoolean - aDouble, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to boolean, double
    "aChar - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to char, boolean
    "aByte - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to byte, boolean
    "aShort - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to short, boolean
    "anInt - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to int, boolean
    "aLong - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to long, boolean
    "aFloat - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to float, boolean
    "aDouble - aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // - not applicable to double, boolean
    "aBoolean * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, boolean
    "aBoolean * aChar, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, char
    "aBoolean * aByte, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, byte
    "aBoolean * aShort, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, short
    "aBoolean * anInt, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, int
    "aBoolean * aLong, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, long
    "aBoolean * aFloat, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, float
    "aBoolean * aDouble, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to boolean, double
    "aChar * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to char, boolean
    "aByte * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to byte, boolean
    "aShort * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to short, boolean
    "anInt * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to int, boolean
    "aLong * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to long, boolean
    "aFloat * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to float, boolean
    "aDouble * aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // * not applicable to double, boolean
    "aBoolean / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, boolean
    "aBoolean / aChar, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, char
    "aBoolean / aByte, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, byte
    "aBoolean / aShort, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, short
    "aBoolean / anInt, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, int
    "aBoolean / aLong, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, long
    "aBoolean / aFloat, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, float
    "aBoolean / aDouble, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to boolean, double
    "aChar / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to char, boolean
    "aByte / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to byte, boolean
    "aShort / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to short, boolean
    "anInt / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to int, boolean
    "aLong / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to long, boolean
    "aFloat / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to float, boolean
    "aDouble / aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // / not applicable to double, boolean
    "aBoolean % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, boolean
    "aBoolean % aChar, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, char
    "aBoolean % aByte, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, byte
    "aBoolean % aShort, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, short
    "aBoolean % anInt, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, int
    "aBoolean % aLong, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, long
    "aBoolean % aFloat, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, float
    "aBoolean % aDouble, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to boolean, double
    "aChar % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to char, boolean
    "aByte % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to byte, boolean
    "aShort % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to short, boolean
    "anInt % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to int, boolean
    "aLong % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to long, boolean
    "aFloat % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to float, boolean
    "aDouble % aBoolean, EXPR_NUMERICAL_OP_NOT_APPLICABLE", // % not applicable to double, boolean
    "aChar = 0 + -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = -1 + 0, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 0 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 65535 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 1 + 65535, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 1 * -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = -1 * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 32768 * 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 32767 * 2 + 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 32768 * -2 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 65536 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 1 / -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = -1 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aChar + aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aChar - aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aChar * aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aChar / aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = aChar % aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided int
    "aChar = 1 + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1l + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1 + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1f + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 1 + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 0.1 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 1 - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1l - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1 - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1f - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 1 - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 0.1 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 1 * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1l * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1 * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1f * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 1 * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 0.1 * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 1 / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1l / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1 / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1f / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 1 / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 0.1 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 1 % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1l % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided long
    "aChar = 1 % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 0.1f % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided float
    "aChar = 1 % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aChar = 0.1 % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided double
    "aByte = 127 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 1 + 127, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 64 * 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 63 * 2 + 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 64 * -2 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 128 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 129 / -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aByte + aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aByte - aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aByte * aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aByte / aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = aByte % aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided int
    "aByte = 1 + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1l + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1 + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1f + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 1 + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 0.1 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 1 - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1l - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1 - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1f - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 1 - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 0.1 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 1 * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1l * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1 * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1f * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 1 * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 0.1 * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 1 / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1l / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1 / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1f / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 1 / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 0.1 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 1 % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1l % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided long
    "aByte = 1 % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 0.1f % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided float
    "aByte = 1 % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aByte = 0.1 % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided double
    "aShort = 32767 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 1 + 32767, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 16384 * 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 16383 * 2 + 2, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 16384 * -2 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 32768 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 32769 / -1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aShort + aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aShort - aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aShort * aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aShort / aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = aShort % aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided int
    "aShort = 1 + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1l + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1 + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1f + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 1 + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 0.1 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 1 - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1l - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1 - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1f - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 1 - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 0.1 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 1 * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1l * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1 * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1f * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 1 * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 0.1 * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 1 / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1l / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1 / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1f / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 1 / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 0.1 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 1 % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1l % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided long
    "aShort = 1 % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 0.1f % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided float
    "aShort = 1 % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "aShort = 0.1 % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided double
    "anInt = 1 + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1l + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1 + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1f + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 1 + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 0.1 + 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 1 - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1l - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1 - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1f - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 1 - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 0.1 - 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 1 * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1l * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1 * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1f * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 1 * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 0.1 * 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 1 / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1l / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1 / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1f / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 1 / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 0.1 / 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 1 % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1l % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided long
    "anInt = 1 % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 0.1f % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided float
    "anInt = 1 % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "anInt = 0.1 % 1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided double
    "aLong = 1l + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1f + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 1l + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 0.1 + 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 1l - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1f - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 1l - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 0.1 - 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 1l * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1f * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 1l * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 0.1 * 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 1l / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1f / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 1l / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 0.1 / 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 1l % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 0.1f % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided float
    "aLong = 1l % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aLong = 0.1 % 1l, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided double
    "aFloat = 0.1f + 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1 + 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1f - 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1 - 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1f * 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1 * 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1f / 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1 / 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1f % 0.1, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aFloat = 0.1 % 0.1f, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided double
    "aBoolean && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, char
    "aBoolean && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, byte
    "aBoolean && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, short
    "aBoolean && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, int
    "aBoolean && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, long
    "aBoolean && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, float
    "aBoolean && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to boolean, double
    "aChar && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, boolean
    "aChar && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, char
    "aChar && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, byte
    "aChar && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, short
    "aChar && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, int
    "aChar && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, long
    "aChar && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, float
    "aChar && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to char, double
    "aByte && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, boolean
    "aByte && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, char
    "aByte && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, byte
    "aByte && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, short
    "aByte && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, int
    "aByte && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, long
    "aByte && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, float
    "aByte && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to byte, double
    "aShort && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, boolean
    "aShort && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, char
    "aShort && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, byte
    "aShort && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, short
    "aShort && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, int
    "aShort && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, long
    "aShort && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, float
    "aShort && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, double
    "anInt && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to short, boolean
    "anInt && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, char
    "anInt && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, byte
    "anInt && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, short
    "anInt && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, int
    "anInt && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, long
    "anInt && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, float
    "anInt && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to int, double
    "aLong && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, boolean
    "aLong && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, char
    "aLong && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, byte
    "aLong && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, short
    "aLong && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, int
    "aLong && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, long
    "aLong && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, float
    "aLong && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to long, double
    "aFloat && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, boolean
    "aFloat && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, char
    "aFloat && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, byte
    "aFloat && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, short
    "aFloat && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, int
    "aFloat && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, long
    "aFloat && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, float
    "aFloat && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to float, double
    "aDouble && aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, boolean
    "aDouble && aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, char
    "aDouble && aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, byte
    "aDouble && aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, short
    "aDouble && anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, int
    "aDouble && aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, long
    "aDouble && aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, float
    "aDouble && aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // && not applicable to double, double
    "aBoolean || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, char
    "aBoolean || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, byte
    "aBoolean || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, short
    "aBoolean || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, int
    "aBoolean || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, long
    "aBoolean || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, float
    "aBoolean || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to boolean, double
    "aChar || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, boolean
    "aChar || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, char
    "aChar || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, byte
    "aChar || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, short
    "aChar || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, int
    "aChar || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, long
    "aChar || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, float
    "aChar || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to char, double
    "aByte || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, boolean
    "aByte || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, char
    "aByte || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, byte
    "aByte || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, short
    "aByte || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, int
    "aByte || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, long
    "aByte || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, float
    "aByte || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to byte, double
    "aShort || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, boolean
    "aShort || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, char
    "aShort || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, byte
    "aShort || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, short
    "aShort || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, int
    "aShort || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, long
    "aShort || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, float
    "aShort || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, double
    "anInt || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to short, boolean
    "anInt || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, char
    "anInt || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, byte
    "anInt || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, short
    "anInt || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, int
    "anInt || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, long
    "anInt || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, float
    "anInt || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to int, double
    "aLong || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, boolean
    "aLong || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, char
    "aLong || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, byte
    "aLong || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, short
    "aLong || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, int
    "aLong || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, long
    "aLong || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, float
    "aLong || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to long, double
    "aFloat || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, boolean
    "aFloat || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, char
    "aFloat || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, byte
    "aFloat || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, short
    "aFloat || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, int
    "aFloat || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, long
    "aFloat || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, float
    "aFloat || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to float, double
    "aDouble || aBoolean, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, boolean
    "aDouble || aChar, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, char
    "aDouble || aByte, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, byte
    "aDouble || aShort, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, short
    "aDouble || anInt, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, int
    "aDouble || aLong, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, long
    "aDouble || aFloat, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, float
    "aDouble || aDouble, EXPR_LOGICAL_OP_NOT_APPLICABLE", // || not applicable to double, double
    "aBoolean > aChar, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, char
    "aBoolean > aByte, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, byte
    "aBoolean > aShort, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, short
    "aBoolean > anInt, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, int
    "aBoolean > aLong, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, long
    "aBoolean > aFloat, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, float
    "aBoolean > aDouble, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to boolean, double
    "aChar > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to char, boolean
    "aByte > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to byte, boolean
    "aShort > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to short, boolean
    "anInt > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to int, boolean
    "aLong > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to long, boolean
    "aFloat > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to float, boolean
    "aDouble > aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // > not applicable to double, boolean
    "aBoolean < aChar, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, char
    "aBoolean < aByte, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, byte
    "aBoolean < aShort, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, short
    "aBoolean < anInt, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, int
    "aBoolean < aLong, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, long
    "aBoolean < aFloat, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, float
    "aBoolean < aDouble, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to boolean, double
    "aChar < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to char, boolean
    "aByte < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to byte, boolean
    "aShort < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to short, boolean
    "anInt < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to int, boolean
    "aLong < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to long, boolean
    "aFloat < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to float, boolean
    "aDouble < aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // < not applicable to double, boolean
    "aBoolean >= aChar, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, char
    "aBoolean >= aByte, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, byte
    "aBoolean >= aShort, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, short
    "aBoolean >= anInt, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, int
    "aBoolean >= aLong, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, long
    "aBoolean >= aFloat, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, float
    "aBoolean >= aDouble, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to boolean, double
    "aChar >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to char, boolean
    "aByte >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to byte, boolean
    "aShort >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to short, boolean
    "anInt >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to int, boolean
    "aLong >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to long, boolean
    "aFloat >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to float, boolean
    "aDouble >= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // >= not applicable to double, boolean
    "aBoolean <= aChar, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, char
    "aBoolean <= aByte, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, byte
    "aBoolean <= aShort, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, short
    "aBoolean <= anInt, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, int
    "aBoolean <= aLong, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, long
    "aBoolean <= aFloat, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, float
    "aBoolean <= aDouble, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to boolean, double
    "aChar <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to char, boolean
    "aByte <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to byte, boolean
    "aShort <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to short, boolean
    "anInt <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to int, boolean
    "aLong <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to long, boolean
    "aFloat <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to float, boolean
    "aDouble <= aBoolean, EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE", // <= not applicable to double, boolean
    "aBoolean == aChar, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, char
    "aBoolean == aByte, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, byte
    "aBoolean == aShort, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, short
    "aBoolean == anInt, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, int
    "aBoolean == aLong, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, long
    "aBoolean == aFloat, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, float
    "aBoolean == aDouble, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to boolean, double
    "aChar == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to char, boolean
    "aByte == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to byte, boolean
    "aShort == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to short, boolean
    "anInt == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to int, boolean
    "aLong == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to long, boolean
    "aFloat == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to float, boolean
    "aDouble == aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // == not applicable to double, boolean
    "aBoolean != aChar, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, char
    "aBoolean != aByte, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, byte
    "aBoolean != aShort, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, short
    "aBoolean != anInt, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, int
    "aBoolean != aLong, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, long
    "aBoolean != aFloat, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, float
    "aBoolean != aDouble, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to boolean, double
    "aChar != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to char, boolean
    "aByte != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to byte, boolean
    "aShort != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to short, boolean
    "anInt != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to int, boolean
    "aLong != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to long, boolean
    "aFloat != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to float, boolean
    "aDouble != aBoolean, EXPR_EQUAL_OP_NOT_APPLICABLE", // != not applicable to double, boolean
    "aChar = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aBoolean && aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aBoolean || aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar > aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte > aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort > aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt > anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong > aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat > aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble > aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar < aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte < aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort < aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt < anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong < aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat < aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble < aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar >= aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte >= aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort >= aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt >= anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong >= aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat >= aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble >= aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar <= aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte <= aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort <= aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt <= anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong <= aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat <= aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble <= aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar == aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte == aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort == aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt == anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong == aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat == aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble == aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aChar = aChar != aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected char but provided boolean
    "aByte = aByte != aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected byte but provided boolean
    "aShort = aShort != aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected short but provided boolean
    "anInt = anInt != anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected int but provided boolean
    "aLong = aLong != aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected long but provided boolean
    "aFloat = aFloat != aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected float but provided boolean
    "aDouble = aDouble != aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected double but provided boolean
    "aByte ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to byte
    "aShort ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to short
    "aChar ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to char
    "anInt ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to int
    "aLong ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to long
    "aFloat ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to float
    "aDouble ? 0 : 1, EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN", // ? not applicable to double
    "aBoolean = aBoolean ? aByte : aBoolean, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided (byte, boolean)
    "aBoolean = aBoolean ? aBoolean : aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided (boolean, byte)
    "aBoolean = aBoolean ? aByte : aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES", // expected boolean but provided (byte, byte)
    "true << 2, EXPR_SHIFT_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
    "true >> 2, EXPR_SHIFT_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
    "true >>> 2, EXPR_SHIFT_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
    "true & 2, EXPR_BINARY_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
    "true ^ 2, EXPR_BINARY_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
    "true | 2, EXPR_BINARY_OP_NOT_APPLICABLE", // not applicable to (boolean, int)
  })
  public void testInvalidExpression(@NotNull String expr,
                                    @NotNull MCError error) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(error);
    Preconditions.checkArgument(!expr.isBlank());

    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    TypeCheck3.typeOf(ast);

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(error.getErrorCode());
  }
}
