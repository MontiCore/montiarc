/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeAll;
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

/**
 * This class provides tests for validating the correctness of type checking of
 * primitives in expressions.
 * <p>
 * The class under test is {@link MontiArcTypeCalculator}.
 */
public class TypeCheckPrimitivesTest extends MontiArcAbstractTest {

  /**
   * The enclosing scope of the symbols of the test setup
   */
  protected IArcBasisScope scope;

  @BeforeAll
  public static void log() {
    Log.enableFailQuick(false);
  }

  @BeforeEach
  @Override
  public void setUp() {
    super.setUp();
    this.initSymbols();
  }

  protected void initSymbols() {
    this.scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addSubScope(scope);
    scope.setEnclosingScope(MontiArcMill.globalScope());
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
  })
  public void testValidExpression(@NotNull String expr) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkArgument(!expr.isBlank());

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "aBoolean = 'a', 0xA0179", // expected boolean but provided char
    "aBoolean = 0, 0xA0179", // expected boolean but provided int
    "aBoolean = 1, 0xA0179", // expected boolean but provided int
    "aBoolean = -1, 0xA0179", // expected boolean but provided int
    "aBoolean = 1l, 0xA0179", // expected boolean but provided long
    "aBoolean = 0.1f, 0xA0179", // expected boolean but provided float
    "aBoolean = 0.1, 0xA0179", // expected boolean but provided double
    "aBoolean = aChar, 0xA0179", // expected boolean but provided char
    "aBoolean = aShort, 0xA0179", // expected boolean but provided byte
    "aBoolean = aByte, 0xA0179", // expected boolean but provided short
    "aBoolean = anInt, 0xA0179", // expected boolean but provided int
    "aBoolean = aLong, 0xA0179", // expected boolean but provided long
    "aBoolean = aFloat, 0xA0179", // expected boolean but provided float
    "aBoolean = aDouble, 0xA0179", // expected boolean but provided double
    "aChar = true, 0xA0179", // expected char but provided boolean
    "aChar = false, 0xA0179", // expected char but provided boolean
    "aChar = -1, 0xA0179", // expected char but provided int
    "aChar = 65536, 0xA0179", // expected char but provided int
    "aChar = 1l, 0xA0179", // expected char but provided long
    "aChar = 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1, 0xA0179", // expected char but provided double
    "aChar = aBoolean, 0xA0179", // expected char but provided boolean
    "aChar = aByte, 0xA0179", // expected char but provided byte
    "aChar = aShort, 0xA0179", // expected char but provided short
    "aChar = anInt, 0xA0179", // expected char but provided int
    "aChar = aLong, 0xA0179", // expected char but provided long
    "aChar = aFloat, 0xA0179", // expected char but provided float
    "aChar = aDouble, 0xA0179", // expected char but provided double
    "aByte = true, 0xA0179", // expected byte but provided boolean
    "aByte = false, 0xA0179", // expected byte but provided boolean
    "aByte = 128, 0xA0179", // expected byte but provided int
    "aByte = -129, 0xA0179", // expected byte but provided int
    "aByte = 1l, 0xA0179", // expected byte but provided long
    "aByte = 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1, 0xA0179", // expected byte but provided double
    "aByte = aBoolean, 0xA0179", // expected byte but provided boolean
    "aByte = aChar, 0xA0179", // expected byte but provided char
    "aByte = aShort, 0xA0179", // expected byte but provided short
    "aByte = anInt, 0xA0179", // expected byte but provided int
    "aByte = aLong, 0xA0179", // expected byte but provided long
    "aByte = aFloat, 0xA0179", // expected byte but provided float
    "aByte = aDouble, 0xA0179", // expected byte but provided double
    "aShort = true, 0xA0179", // expected short but provided boolean
    "aShort = false, 0xA0179", // expected short but provided boolean
    "aShort = 32768, 0xA0179", // expected short but provided int
    "aShort = -32769, 0xA0179", // expected short but provided int
    "aShort = 1l, 0xA0179", // expected short but provided long
    "aShort = 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1, 0xA0179", // expected short but provided double
    "aShort = aBoolean, 0xA0179", // expected short but provided boolean
    "aShort = aChar, 0xA0179", // expected short but provided char
    "aShort = anInt, 0xA0179", // expected short but provided int
    "aShort = aLong, 0xA0179", // expected short but provided long
    "aShort = aFloat, 0xA0179", // expected short but provided float
    "aShort = aDouble, 0xA0179", // expected short but provided double
    "anInt = true, 0xA0179", // expected int but provided boolean
    "anInt = false, 0xA0179", // expected int but provided boolean
    // "anInt = 2147483648, 0x12345", // integer literal too large
    // "anInt = -2147483649, 0x12345", // integer literal too large
    "anInt = 1l, 0xA0179", // expected int but provided long
    "anInt = 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1, 0xA0179", // expected int but provided double
    "anInt = aBoolean, 0xA0179", // expected int but provided boolean
    "anInt = aLong, 0xA0179", // expected int but provided long
    "anInt = aFloat, 0xA0179", // expected int but provided float
    "anInt = aDouble, 0xA0179", // expected int but provided double
    "aLong = true, 0xA0179", // expected long but provided boolean
    "aLong = false, 0xA0179", // expected long but provided boolean
    // "aLong = 9223372036854775808l, 0x12345", // long literal too large
    // "aLong = -9223372036854775809l, 0x12345", // long literal too large
    "aLong = 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1, 0xA0179", // expected long but provided double
    "aLong = aBoolean, 0xA0179", // expected long but provided boolean
    "aLong = aFloat, 0xA0179", // expected long but provided float
    "aLong = aDouble, 0xA0179", // expected long but provided double
    "aFloat = true, 0xA0179", // expected float but provided boolean
    "aFloat = false, 0xA0179", // expected float but provided boolean
    "aFloat = 0.1, 0xA0179", // expected float but provided double
    "aFloat = aBoolean, 0xA0179", // expected float but provided boolean
    "aFloat = aDouble, 0xA0179", // expected float but provided double
    "aDouble = true, 0xA0179", // expected double but provided boolean
    "aDouble = false, 0xA0179", // expected double but provided boolean
    "aDouble = aBoolean, 0xA0179", // expected double but provided boolean
    "aBoolean += aBoolean, 0xA0178", // += not applicable to boolean, boolean
    "aBoolean += aChar, 0xA0178", // += not applicable to boolean, char
    "aBoolean += aByte, 0xA0178", // += not applicable to boolean, byte
    "aBoolean += aShort, 0xA0178", // += not applicable to boolean, short
    "aBoolean += anInt, 0xA0178", // += not applicable to boolean, int
    "aBoolean += aLong, 0xA0178", // += not applicable to boolean, long
    "aBoolean += aFloat, 0xA0178", // += not applicable to boolean, float
    "aBoolean += aDouble, 0xA0178", // += not applicable to boolean, double
    "aChar += aBoolean, 0xA0178", // += not applicable to char, boolean
    "aByte += aBoolean, 0xA0178", // += not applicable to byte, boolean
    "aShort += aBoolean, 0xA0178", // += not applicable to short, boolean
    "anInt += aBoolean, 0xA0178", // += not applicable to int, boolean
    "aLong += aBoolean, 0xA0178", // += not applicable to long, boolean
    "aFloat += aBoolean, 0xA0178", // += not applicable to float, boolean
    "aDouble += aBoolean, 0xA0178", // += not applicable to double, boolean
    "aBoolean -= aBoolean, 0xA0178", // -= not applicable to boolean, boolean
    "aBoolean -= aChar, 0xA0178", // -= not applicable to boolean, char
    "aBoolean -= aByte, 0xA0178", // -= not applicable to boolean, byte
    "aBoolean -= aShort, 0xA0178", // -= not applicable to boolean, short
    "aBoolean -= anInt, 0xA0178", // -= not applicable to boolean, int
    "aBoolean -= aLong, 0xA0178", // -= not applicable to boolean, long
    "aBoolean -= aFloat, 0xA0178", // -= not applicable to boolean, float
    "aBoolean -= aDouble, 0xA0178", // -= not applicable to boolean, double
    "aBoolean *= aBoolean, 0xA0178", // *= not applicable to boolean, boolean
    "aBoolean *= aChar, 0xA0178", // *= not applicable to boolean, char
    "aBoolean *= aByte, 0xA0178", // *= not applicable to boolean, byte
    "aBoolean *= aShort, 0xA0178", // *= not applicable to boolean, short
    "aBoolean *= anInt, 0xA0178", // *= not applicable to boolean, int
    "aBoolean *= aLong, 0xA0178", // *= not applicable to boolean, long
    "aBoolean *= aFloat, 0xA0178", // *= not applicable to boolean, float
    "aBoolean *= aDouble, 0xA0178", // *= not applicable to boolean, double
    "aBoolean /= aBoolean, 0xA0178", // /= not applicable to boolean, boolean
    "aBoolean /= aChar, 0xA0178", // /= not applicable to boolean, char
    "aBoolean /= aByte, 0xA0178", // /= not applicable to boolean, byte
    "aBoolean /= aShort, 0xA0178", // /= not applicable to boolean, short
    "aBoolean /= anInt, 0xA0178", // /= not applicable to boolean, int
    "aBoolean /= aLong, 0xA0178", // /= not applicable to boolean, long
    "aBoolean /= aFloat, 0xA0178", // /= not applicable to boolean, float
    "aBoolean /= aDouble, 0xA0178", // /= not applicable to boolean, double
    "aBoolean %= aBoolean, 0xA0178", // %= not applicable to boolean, boolean
    "aBoolean %= aChar, 0xA0178", // %= not applicable to boolean, char
    "aBoolean %= aByte, 0xA0178", // %= not applicable to boolean, byte
    "aBoolean %= aShort, 0xA0178", // %= not applicable to boolean, short
    "aBoolean %= anInt, 0xA0178", // %= not applicable to boolean, int
    "aBoolean %= aLong, 0xA0178", // %= not applicable to boolean, long
    "aBoolean %= aFloat, 0xA0178", // %= not applicable to boolean, float
    "aBoolean %= aDouble, 0xA0178", // %= not applicable to boolean, double
    "aBoolean >>= aBoolean, 0xA0177", // >>= not applicable to boolean, boolean
    "aBoolean >>= aChar, 0xA0177", // >>= not applicable to boolean, char
    "aBoolean >>= aByte, 0xA0177", // >>= not applicable to boolean, byte
    "aBoolean >>= aShort, 0xA0177", // >>= not applicable to boolean, short
    "aBoolean >>= anInt, 0xA0177", // >>= not applicable to boolean, int
    "aBoolean >>= aLong, 0xA0177", // >>= not applicable to boolean, long
    "aBoolean >>= aFloat, 0xA0177", // >>= not applicable to boolean, float
    "aBoolean >>= aDouble, 0xA0177", // >>= not applicable to boolean, double
    "aFloat >>= aBoolean, 0xA0177", // >>= not applicable to float, boolean
    "aFloat >>= aChar, 0xA0177", // >>= not applicable to float, char
    "aFloat >>= aByte, 0xA0177", // >>= not applicable to float, byte
    "aFloat >>= aShort, 0xA0177", // >>= not applicable to float, short
    "aFloat >>= anInt, 0xA0177", // >>= not applicable to float, int
    "aFloat >>= aLong, 0xA0177", // >>= not applicable to float, long
    "aFloat >>= aFloat, 0xA0177", // >>= not applicable to float, float
    "aFloat >>= aDouble, 0xA0177", // >>= not applicable to float, double
    "aDouble >>= aBoolean, 0xA0177", // >>= not applicable to double, boolean
    "aDouble >>= aChar, 0xA0177", // >>= not applicable to double, char
    "aDouble >>= aByte, 0xA0177", // >>= not applicable to double, byte
    "aDouble >>= aShort, 0xA0177", // >>= not applicable to double, short
    "aDouble >>= anInt, 0xA0177", // >>= not applicable to double, int
    "aDouble >>= aLong, 0xA0177", // >>= not applicable to double, long
    "aDouble >>= aFloat, 0xA0177", // >>= not applicable to double, float
    "aDouble >>= aDouble, 0xA0177", // >>= not applicable to double, double
    "aBoolean <<= aBoolean, 0xA0177", // <<= not applicable to boolean, boolean
    "aBoolean <<= aChar, 0xA0177", // <<= not applicable to boolean, char
    "aBoolean <<= aByte, 0xA0177", // <<= not applicable to boolean, byte
    "aBoolean <<= aShort, 0xA0177", // <<= not applicable to boolean, short
    "aBoolean <<= anInt, 0xA0177", // <<= not applicable to boolean, int
    "aBoolean <<= aLong, 0xA0177", // <<= not applicable to boolean, long
    "aBoolean <<= aFloat, 0xA0177", // <<= not applicable to boolean, float
    "aBoolean <<= aDouble, 0xA0177", // <<= not applicable to boolean, double
    "aFloat <<= aBoolean, 0xA0177", // <<= not applicable to float, boolean
    "aFloat <<= aChar, 0xA0177", // <<= not applicable to float, char
    "aFloat <<= aByte, 0xA0177", // <<= not applicable to float, byte
    "aFloat <<= aShort, 0xA0177", // <<= not applicable to float, short
    "aFloat <<= anInt, 0xA0177", // <<= not applicable to float, int
    "aFloat <<= aLong, 0xA0177", // <<= not applicable to float, long
    "aFloat <<= aFloat, 0xA0177", // <<= not applicable to float, float
    "aFloat <<= aDouble, 0xA0177", // <<= not applicable to float, double
    "aDouble <<= aBoolean, 0xA0177", // <<= not applicable to double, boolean
    "aDouble <<= aChar, 0xA0177", // <<= not applicable to double, char
    "aDouble <<= aByte, 0xA0177", // <<= not applicable to double, byte
    "aDouble <<= aShort, 0xA0177", // <<= not applicable to double, short
    "aDouble <<= anInt, 0xA0177", // <<= not applicable to double, int
    "aDouble <<= aLong, 0xA0177", // <<= not applicable to double, long
    "aDouble <<= aFloat, 0xA0177", // <<= not applicable to double, float
    "aDouble <<= aDouble, 0xA0177", // <<= not applicable to double, double
    "aBoolean >>>= aBoolean, 0xA0177", // >>>= not applicable to boolean, boolean
    "aBoolean >>>= aChar, 0xA0177", // >>>= not applicable to boolean, char
    "aBoolean >>>= aByte, 0xA0177", // >>>= not applicable to boolean, byte
    "aBoolean >>>= aShort, 0xA0177", // >>>= not applicable to boolean, short
    "aBoolean >>>= anInt, 0xA0177", // >>>= not applicable to boolean, int
    "aBoolean >>>= aLong, 0xA0177", // >>>= not applicable to boolean, long
    "aBoolean >>>= aFloat, 0xA0177", // >>>= not applicable to boolean, float
    "aBoolean >>>= aDouble, 0xA0177", // >>>= not applicable to boolean, double
    "aFloat >>>= aBoolean, 0xA0177", // >>>= not applicable to float, boolean
    "aFloat >>>= aChar, 0xA0177", // >>>= not applicable to float, char
    "aFloat >>>= aByte, 0xA0177", // >>>= not applicable to float, byte
    "aFloat >>>= aShort, 0xA0177", // >>>= not applicable to float, short
    "aFloat >>>= anInt, 0xA0177", // >>>= not applicable to float, int
    "aFloat >>>= aLong, 0xA0177", // >>>= not applicable to float, long
    "aFloat >>>= aFloat, 0xA0177", // >>>= not applicable to float, float
    "aFloat >>>= aDouble, 0xA0177", // >>>= not applicable to float, double
    "aDouble >>>= aBoolean, 0xA0177", // >>>= not applicable to double, boolean
    "aDouble >>>= aChar, 0xA0177", // >>>= not applicable to double, char
    "aDouble >>>= aByte, 0xA0177", // >>>= not applicable to double, byte
    "aDouble >>>= aShort, 0xA0177", // >>>= not applicable to double, short
    "aDouble >>>= anInt, 0xA0177", // >>>= not applicable to double, int
    "aDouble >>>= aLong, 0xA0177", // >>>= not applicable to double, long
    "aDouble >>>= aFloat, 0xA0177", // >>>= not applicable to double, float
    "aDouble >>>= aDouble, 0xA0177", // >>>= not applicable to double, double
    "aBoolean &= aChar, 0xA0176", // &= not applicable to boolean, char
    "aBoolean &= aByte, 0xA0176", // &= not applicable to boolean, byte
    "aBoolean &= aShort, 0xA0176", // &= not applicable to boolean, short
    "aBoolean &= anInt, 0xA0176", // &= not applicable to boolean, int
    "aBoolean &= aLong, 0xA0176", // &= not applicable to boolean, long
    "aBoolean &= aFloat, 0xA0176", // &= not applicable to boolean, float
    "aBoolean &= aDouble, 0xA0176", // &= not applicable to boolean, double
    "aChar &= aBoolean, 0xA0176", // &= not applicable to char, boolean
    "aChar &= aFloat, 0xA0176", // &= not applicable to char, float
    "aChar &= aDouble, 0xA0176", // &= not applicable to char, double
    "aByte &= aBoolean, 0xA0176", // &= not applicable to byte, boolean
    "aByte &= aFloat, 0xA0176", // &= not applicable to byte, float
    "aByte &= aDouble, 0xA0176", // &= not applicable to byte, double
    "aShort &= aBoolean, 0xA0176", // &= not applicable to short, boolean
    "aShort &= aFloat, 0xA0176", // &= not applicable to short, float
    "aShort &= aDouble, 0xA0176", // &= not applicable to short, double
    "anInt &= aBoolean, 0xA0176", // &= not applicable to int, boolean
    "anInt &= aFloat, 0xA0176", // &= not applicable to int, float
    "anInt &= aDouble, 0xA0176", // &= not applicable to int, double
    "aLong &= aBoolean, 0xA0176", // &= not applicable to long, boolean
    "aLong &= aFloat, 0xA0176", // &= not applicable to long, float
    "aLong &= aDouble, 0xA0176", // &= not applicable to long, double
    "aFloat &= aBoolean, 0xA0176", // &= not applicable to float, boolean
    "aFloat &= aChar, 0xA0176", // &= not applicable to float, char
    "aFloat &= aByte, 0xA0176", // &= not applicable to float, byte
    "aFloat &= aShort, 0xA0176", // &= not applicable to float, short
    "aFloat &= anInt, 0xA0176", // &= not applicable to float, int
    "aFloat &= aLong, 0xA0176", // &= not applicable to float, long
    "aFloat &= aFloat, 0xA0176", // &= not applicable to float, float
    "aFloat &= aDouble, 0xA0176", // &= not applicable to float, double
    "aDouble &= aBoolean, 0xA0176", // &= not applicable to double, boolean
    "aDouble &= aChar, 0xA0176", // &= not applicable to double, char
    "aDouble &= aByte, 0xA0176", // &= not applicable to double, byte
    "aDouble &= aShort, 0xA0176", // &= not applicable to double, short
    "aDouble &= anInt, 0xA0176", // &= not applicable to double, int
    "aDouble &= aLong, 0xA0176", // &= not applicable to double, long
    "aDouble &= aFloat, 0xA0176", // &= not applicable to double, float
    "aDouble &= aDouble, 0xA0176", // &= not applicable double, double
    "aBoolean |= aChar, 0xA0176", // |= not applicable to boolean, char
    "aBoolean |= aByte, 0xA0176", // |= not applicable to boolean, byte
    "aBoolean |= aShort, 0xA0176", // |= not applicable to boolean, short
    "aBoolean |= anInt, 0xA0176", // |= not applicable to boolean, int
    "aBoolean |= aLong, 0xA0176", // |= not applicable to boolean, long
    "aBoolean |= aFloat, 0xA0176", // |= not applicable to boolean, float
    "aBoolean |= aDouble, 0xA0176", // |= not applicable to boolean, double
    "aChar |= aBoolean, 0xA0176", // |= not applicable to char, boolean
    "aChar |= aFloat, 0xA0176", // |= not applicable to char, float
    "aChar |= aDouble, 0xA0176", // |= not applicable to char, double
    "aByte |= aBoolean, 0xA0176", // |= not applicable to byte, boolean
    "aByte |= aFloat, 0xA0176", // |= not applicable to byte, float
    "aByte |= aDouble, 0xA0176", // |= not applicable to byte, double
    "aShort |= aBoolean, 0xA0176", // |= not applicable to short, boolean
    "aShort |= aFloat, 0xA0176", // |= not applicable to short, float
    "aShort |= aDouble, 0xA0176", // |= not applicable to short, double
    "anInt |= aBoolean, 0xA0176", // |= not applicable to int, boolean
    "anInt |= aFloat, 0xA0176", // |= not applicable to int, float
    "anInt |= aDouble, 0xA0176", // |= not applicable to int, double
    "aLong |= aBoolean, 0xA0176", // |= not applicable to long, boolean
    "aLong |= aFloat, 0xA0176", // |= not applicable to long, float
    "aLong |= aDouble, 0xA0176", // |= not applicable to long, double
    "aFloat |= aBoolean, 0xA0176", // |= not applicable to float, boolean
    "aFloat |= aChar, 0xA0176", // |= not applicable to float, char
    "aFloat |= aByte, 0xA0176", // |= not applicable to float, byte
    "aFloat |= aShort, 0xA0176", // |= not applicable to float, short
    "aFloat |= anInt, 0xA0176", // |= not applicable to float, int
    "aFloat |= aLong, 0xA0176", // |= not applicable to float, long
    "aFloat |= aFloat, 0xA0176", // |= not applicable to float, float
    "aFloat |= aDouble, 0xA0176", // |= not applicable to float, double
    "aDouble |= aBoolean, 0xA0176", // |= not applicable to double, boolean
    "aDouble |= aChar, 0xA0176", // |= not applicable to double, char
    "aDouble |= aByte, 0xA0176", // |= not applicable to double, byte
    "aDouble |= aShort, 0xA0176", // |= not applicable to double, short
    "aDouble |= anInt, 0xA0176", // |= not applicable to double, int
    "aDouble |= aLong, 0xA0176", // |= not applicable to double, long
    "aDouble |= aFloat, 0xA0176", // |= not applicable to double, float
    "aDouble |= aDouble, 0xA0176", // |= not applicable double, double
    "aBoolean ^= aChar, 0xA0176", // ^= not applicable to boolean, char
    "aBoolean ^= aByte, 0xA0176", // ^= not applicable to boolean, byte
    "aBoolean ^= aShort, 0xA0176", // ^= not applicable to boolean, short
    "aBoolean ^= anInt, 0xA0176", // ^= not applicable to boolean, int
    "aBoolean ^= aLong, 0xA0176", // ^= not applicable to boolean, long
    "aBoolean ^= aFloat, 0xA0176", // ^= not applicable to boolean, float
    "aBoolean ^= aDouble, 0xA0176", // ^= not applicable to boolean, double
    "aChar ^= aBoolean, 0xA0176", // ^= not applicable to char, boolean
    "aChar ^= aFloat, 0xA0176", // ^= not applicable to char, float
    "aChar ^= aDouble, 0xA0176", // ^= not applicable to char, double
    "aByte ^= aBoolean, 0xA0176", // ^= not applicable to byte, boolean
    "aByte ^= aFloat, 0xA0176", // ^= not applicable to byte, float
    "aByte ^= aDouble, 0xA0176", // ^= not applicable to byte, double
    "aShort ^= aBoolean, 0xA0176", // ^= not applicable to short, boolean
    "aShort ^= aFloat, 0xA0176", // ^= not applicable to short, float
    "aShort ^= aDouble, 0xA0176", // ^= not applicable to short, double
    "anInt ^= aBoolean, 0xA0176", // ^= not applicable to int, boolean
    "anInt ^= aFloat, 0xA0176", // ^= not applicable to int, float
    "anInt ^= aDouble, 0xA0176", // ^= not applicable to int, double
    "aLong ^= aBoolean, 0xA0176", // ^= not applicable to long, boolean
    "aLong ^= aFloat, 0xA0176", // ^= not applicable to long, float
    "aLong ^= aDouble, 0xA0176", // ^= not applicable to long, double
    "aFloat ^= aBoolean, 0xA0176", // ^= not applicable to float, boolean
    "aFloat ^= aChar, 0xA0176", // ^= not applicable to float, char
    "aFloat ^= aByte, 0xA0176", // ^= not applicable to float, byte
    "aFloat ^= aShort, 0xA0176", // ^= not applicable to float, short
    "aFloat ^= anInt, 0xA0176", // ^= not applicable to float, int
    "aFloat ^= aLong, 0xA0176", // ^= not applicable to float, long
    "aFloat ^= aFloat, 0xA0176", // ^= not applicable to float, float
    "aFloat ^= aDouble, 0xA0176", // ^= not applicable to float, double
    "aDouble ^= aBoolean, 0xA0176", // ^= not applicable to double, boolean
    "aDouble ^= aChar, 0xA0176", // ^= not applicable to double, char
    "aDouble ^= aByte, 0xA0176", // ^= not applicable to double, byte
    "aDouble ^= aShort, 0xA0176", // ^= not applicable to double, short
    "aDouble ^= anInt, 0xA0176", // ^= not applicable to double, int
    "aDouble ^= aLong, 0xA0176", // ^= not applicable to double, long
    "aDouble ^= aFloat, 0xA0176", // ^= not applicable to double, float
    "aDouble ^= aDouble, 0xA0176", // ^= not applicable to double, double
    "++aBoolean, 0xA0184", // ++ not applicable to boolean
    "--aBoolean, 0xA0184", // -- not applicable to boolean
    "aBoolean++, 0xA0184", // ++ not applicable to boolean
    "aBoolean--, 0xA0184", // -- not applicable to boolean
    "+aBoolean, 0xA017D", // + not applicable to boolean
    "-aBoolean, 0xA017D", // - not applicable to boolean
    "~aBoolean, 0xB0175", // ! not applicable to boolean
    "~aFloat, 0xB0175", // ! not applicable to boolean
    "~aDouble, 0xB0175", // ! not applicable to boolean
    "!aChar, 0xB0164", // ! not applicable to char
    "!aByte, 0xB0164", // ! not applicable to byte
    "!aShort, 0xB0164", // ! not applicable to short
    "!anInt, 0xB0164", // ! not applicable to int
    "!aLong, 0xB0164", // ! not applicable to long
    "!aFloat, 0xB0164", // ! not applicable to float
    "!aDouble, 0xB0164", // ! not applicable to double
    "!aChar, 0xB0164", // ! not applicable to char
    "!aByte, 0xB0164", // ! not applicable to byte
    "!aShort, 0xB0164", // ! not applicable to short
    "!anInt, 0xB0164", // ! not applicable to int
    "!aLong, 0xB0164", // ! not applicable to long
    "!aFloat, 0xB0164", // ! not applicable to float
    "!aDouble, 0xB0164", // ! not applicable to double
    "aChar = ~aChar, 0xA0179", // ~ applicable to char, but result is int
    "aByte = ~aByte, 0xA0179", // ~ applicable to byte, but result is int
    "aShort = ~aShort, 0xA0179", // ~ applicable to short, but result is int
    "aBoolean + aBoolean, 0xB0163", // + not applicable to boolean, boolean
    "aBoolean + aChar, 0xB0163", // + not applicable to boolean, char
    "aBoolean + aByte, 0xB0163", // + not applicable to boolean, byte
    "aBoolean + aShort, 0xB0163", // + not applicable to boolean, short
    "aBoolean + anInt, 0xB0163", // + not applicable to boolean, int
    "aBoolean + aLong, 0xB0163", // + not applicable to boolean, long
    "aBoolean + aFloat, 0xB0163", // + not applicable to boolean, float
    "aBoolean + aDouble, 0xB0163", // + not applicable to boolean, double
    "aChar + aBoolean, 0xB0163", // + not applicable to char, boolean
    "aByte + aBoolean, 0xB0163", // + not applicable to byte, boolean
    "aShort + aBoolean, 0xB0163", // + not applicable to short, boolean
    "anInt + aBoolean, 0xB0163", // + not applicable to int, boolean
    "aLong + aBoolean, 0xB0163", // + not applicable to long, boolean
    "aFloat + aBoolean, 0xB0163", // + not applicable to float, boolean
    "aDouble + aBoolean, 0xB0163", // + not applicable to double, boolean
    "aBoolean - aBoolean, 0xB0163", // - not applicable to boolean, boolean
    "aBoolean - aChar, 0xB0163", // - not applicable to boolean, char
    "aBoolean - aByte, 0xB0163", // - not applicable to boolean, byte
    "aBoolean - aShort, 0xB0163", // - not applicable to boolean, short
    "aBoolean - anInt, 0xB0163", // - not applicable to boolean, int
    "aBoolean - aLong, 0xB0163", // - not applicable to boolean, long
    "aBoolean - aFloat, 0xB0163", // - not applicable to boolean, float
    "aBoolean - aDouble, 0xB0163", // - not applicable to boolean, double
    "aChar - aBoolean, 0xB0163", // - not applicable to char, boolean
    "aByte - aBoolean, 0xB0163", // - not applicable to byte, boolean
    "aShort - aBoolean, 0xB0163", // - not applicable to short, boolean
    "anInt - aBoolean, 0xB0163", // - not applicable to int, boolean
    "aLong - aBoolean, 0xB0163", // - not applicable to long, boolean
    "aFloat - aBoolean, 0xB0163", // - not applicable to float, boolean
    "aDouble - aBoolean, 0xB0163", // - not applicable to double, boolean
    "aBoolean * aBoolean, 0xB0163", // * not applicable to boolean, boolean
    "aBoolean * aChar, 0xB0163", // * not applicable to boolean, char
    "aBoolean * aByte, 0xB0163", // * not applicable to boolean, byte
    "aBoolean * aShort, 0xB0163", // * not applicable to boolean, short
    "aBoolean * anInt, 0xB0163", // * not applicable to boolean, int
    "aBoolean * aLong, 0xB0163", // * not applicable to boolean, long
    "aBoolean * aFloat, 0xB0163", // * not applicable to boolean, float
    "aBoolean * aDouble, 0xB0163", // * not applicable to boolean, double
    "aChar * aBoolean, 0xB0163", // * not applicable to char, boolean
    "aByte * aBoolean, 0xB0163", // * not applicable to byte, boolean
    "aShort * aBoolean, 0xB0163", // * not applicable to short, boolean
    "anInt * aBoolean, 0xB0163", // * not applicable to int, boolean
    "aLong * aBoolean, 0xB0163", // * not applicable to long, boolean
    "aFloat * aBoolean, 0xB0163", // * not applicable to float, boolean
    "aDouble * aBoolean, 0xB0163", // * not applicable to double, boolean
    "aBoolean / aBoolean, 0xB0163", // / not applicable to boolean, boolean
    "aBoolean / aChar, 0xB0163", // / not applicable to boolean, char
    "aBoolean / aByte, 0xB0163", // / not applicable to boolean, byte
    "aBoolean / aShort, 0xB0163", // / not applicable to boolean, short
    "aBoolean / anInt, 0xB0163", // / not applicable to boolean, int
    "aBoolean / aLong, 0xB0163", // / not applicable to boolean, long
    "aBoolean / aFloat, 0xB0163", // / not applicable to boolean, float
    "aBoolean / aDouble, 0xB0163", // / not applicable to boolean, double
    "aChar / aBoolean, 0xB0163", // / not applicable to char, boolean
    "aByte / aBoolean, 0xB0163", // / not applicable to byte, boolean
    "aShort / aBoolean, 0xB0163", // / not applicable to short, boolean
    "anInt / aBoolean, 0xB0163", // / not applicable to int, boolean
    "aLong / aBoolean, 0xB0163", // / not applicable to long, boolean
    "aFloat / aBoolean, 0xB0163", // / not applicable to float, boolean
    "aDouble / aBoolean, 0xB0163", // / not applicable to double, boolean
    "aBoolean % aBoolean, 0xB0163", // % not applicable to boolean, boolean
    "aBoolean % aChar, 0xB0163", // % not applicable to boolean, char
    "aBoolean % aByte, 0xB0163", // % not applicable to boolean, byte
    "aBoolean % aShort, 0xB0163", // % not applicable to boolean, short
    "aBoolean % anInt, 0xB0163", // % not applicable to boolean, int
    "aBoolean % aLong, 0xB0163", // % not applicable to boolean, long
    "aBoolean % aFloat, 0xB0163", // % not applicable to boolean, float
    "aBoolean % aDouble, 0xB0163", // % not applicable to boolean, double
    "aChar % aBoolean, 0xB0163", // % not applicable to char, boolean
    "aByte % aBoolean, 0xB0163", // % not applicable to byte, boolean
    "aShort % aBoolean, 0xB0163", // % not applicable to short, boolean
    "anInt % aBoolean, 0xB0163", // % not applicable to int, boolean
    "aLong % aBoolean, 0xB0163", // % not applicable to long, boolean
    "aFloat % aBoolean, 0xB0163", // % not applicable to float, boolean
    "aDouble % aBoolean, 0xB0163", // % not applicable to double, boolean
    "aChar = 0 + -1, 0xA0179", // expected char but provided int
    "aChar = -1 + 0, 0xA0179", // expected char but provided int
    "aChar = 0 - 1, 0xA0179", // expected char but provided int
    "aChar = 65535 + 1, 0xA0179", // expected char but provided int
    "aChar = 1 + 65535, 0xA0179", // expected char but provided int
    "aChar = 1 * -1, 0xA0179", // expected char but provided int
    "aChar = -1 * 1, 0xA0179", // expected char but provided int
    "aChar = 32768 * 2, 0xA0179", // expected char but provided int
    "aChar = 32767 * 2 + 2, 0xA0179", // expected char but provided int
    "aChar = 32768 * -2 - 1, 0xA0179", // expected char but provided int
    "aChar = 65536 / 1, 0xA0179", // expected char but provided int
    "aChar = 1 / -1, 0xA0179", // expected char but provided int
    "aChar = -1 / 1, 0xA0179", // expected char but provided int
    "aChar = aChar + aChar, 0xA0179", // expected char but provided int
    "aChar = aChar - aChar, 0xA0179", // expected char but provided int
    "aChar = aChar * aChar, 0xA0179", // expected char but provided int
    "aChar = aChar / aChar, 0xA0179", // expected char but provided int
    "aChar = aChar % aChar, 0xA0179", // expected char but provided int
    "aChar = 1 + 1l, 0xA0179", // expected char but provided long
    "aChar = 1l + 1, 0xA0179", // expected char but provided long
    "aChar = 1 + 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1f + 1, 0xA0179", // expected char but provided float
    "aChar = 1 + 0.1, 0xA0179", // expected char but provided double
    "aChar = 0.1 + 1, 0xA0179", // expected char but provided double
    "aChar = 1 - 1l, 0xA0179", // expected char but provided long
    "aChar = 1l - 1, 0xA0179", // expected char but provided long
    "aChar = 1 - 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1f - 1, 0xA0179", // expected char but provided float
    "aChar = 1 - 0.1, 0xA0179", // expected char but provided double
    "aChar = 0.1 - 1, 0xA0179", // expected char but provided double
    "aChar = 1 * 1l, 0xA0179", // expected char but provided long
    "aChar = 1l * 1, 0xA0179", // expected char but provided long
    "aChar = 1 * 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1f * 1, 0xA0179", // expected char but provided float
    "aChar = 1 * 0.1, 0xA0179", // expected char but provided double
    "aChar = 0.1 * 1, 0xA0179", // expected char but provided double
    "aChar = 1 / 1l, 0xA0179", // expected char but provided long
    "aChar = 1l / 1, 0xA0179", // expected char but provided long
    "aChar = 1 / 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1f / 1, 0xA0179", // expected char but provided float
    "aChar = 1 / 0.1, 0xA0179", // expected char but provided double
    "aChar = 0.1 / 1, 0xA0179", // expected char but provided double
    "aChar = 1 % 1l, 0xA0179", // expected char but provided long
    "aChar = 1l % 1, 0xA0179", // expected char but provided long
    "aChar = 1 % 0.1f, 0xA0179", // expected char but provided float
    "aChar = 0.1f % 1, 0xA0179", // expected char but provided float
    "aChar = 1 % 0.1, 0xA0179", // expected char but provided double
    "aChar = 0.1 % 1, 0xA0179", // expected char but provided double
    "aByte = 127 + 1, 0xA0179", // expected byte but provided int
    "aByte = 1 + 127, 0xA0179", // expected byte but provided int
    "aByte = 64 * 2, 0xA0179", // expected byte but provided int
    "aByte = 63 * 2 + 2, 0xA0179", // expected byte but provided int
    "aByte = 64 * -2 - 1, 0xA0179", // expected byte but provided int
    "aByte = 128 / 1, 0xA0179", // expected byte but provided int
    "aByte = 129 / -1, 0xA0179", // expected byte but provided int
    "aByte = aByte + aByte, 0xA0179", // expected byte but provided int
    "aByte = aByte - aByte, 0xA0179", // expected byte but provided int
    "aByte = aByte * aByte, 0xA0179", // expected byte but provided int
    "aByte = aByte / aByte, 0xA0179", // expected byte but provided int
    "aByte = aByte % aByte, 0xA0179", // expected byte but provided int
    "aByte = 1 + 1l, 0xA0179", // expected byte but provided long
    "aByte = 1l + 1, 0xA0179", // expected byte but provided long
    "aByte = 1 + 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1f + 1, 0xA0179", // expected byte but provided float
    "aByte = 1 + 0.1, 0xA0179", // expected byte but provided double
    "aByte = 0.1 + 1, 0xA0179", // expected byte but provided double
    "aByte = 1 - 1l, 0xA0179", // expected byte but provided long
    "aByte = 1l - 1, 0xA0179", // expected byte but provided long
    "aByte = 1 - 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1f - 1, 0xA0179", // expected byte but provided float
    "aByte = 1 - 0.1, 0xA0179", // expected byte but provided double
    "aByte = 0.1 - 1, 0xA0179", // expected byte but provided double
    "aByte = 1 * 1l, 0xA0179", // expected byte but provided long
    "aByte = 1l * 1, 0xA0179", // expected byte but provided long
    "aByte = 1 * 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1f * 1, 0xA0179", // expected byte but provided float
    "aByte = 1 * 0.1, 0xA0179", // expected byte but provided double
    "aByte = 0.1 * 1, 0xA0179", // expected byte but provided double
    "aByte = 1 / 1l, 0xA0179", // expected byte but provided long
    "aByte = 1l / 1, 0xA0179", // expected byte but provided long
    "aByte = 1 / 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1f / 1, 0xA0179", // expected byte but provided float
    "aByte = 1 / 0.1, 0xA0179", // expected byte but provided double
    "aByte = 0.1 / 1, 0xA0179", // expected byte but provided double
    "aByte = 1 % 1l, 0xA0179", // expected byte but provided long
    "aByte = 1l % 1, 0xA0179", // expected byte but provided long
    "aByte = 1 % 0.1f, 0xA0179", // expected byte but provided float
    "aByte = 0.1f % 1, 0xA0179", // expected byte but provided float
    "aByte = 1 % 0.1, 0xA0179", // expected byte but provided double
    "aByte = 0.1 % 1, 0xA0179", // expected byte but provided double
    "aShort = 32767 + 1, 0xA0179", // expected short but provided int
    "aShort = 1 + 32767, 0xA0179", // expected short but provided int
    "aShort = 16384 * 2, 0xA0179", // expected short but provided int
    "aShort = 16383 * 2 + 2, 0xA0179", // expected short but provided int
    "aShort = 16384 * -2 - 1, 0xA0179", // expected short but provided int
    "aShort = 32768 / 1, 0xA0179", // expected short but provided int
    "aShort = 32769 / -1, 0xA0179", // expected short but provided int
    "aShort = aShort + aShort, 0xA0179", // expected short but provided int
    "aShort = aShort - aShort, 0xA0179", // expected short but provided int
    "aShort = aShort * aShort, 0xA0179", // expected short but provided int
    "aShort = aShort / aShort, 0xA0179", // expected short but provided int
    "aShort = aShort % aShort, 0xA0179", // expected short but provided int
    "aShort = 1 + 1l, 0xA0179", // expected short but provided long
    "aShort = 1l + 1, 0xA0179", // expected short but provided long
    "aShort = 1 + 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1f + 1, 0xA0179", // expected short but provided float
    "aShort = 1 + 0.1, 0xA0179", // expected short but provided double
    "aShort = 0.1 + 1, 0xA0179", // expected short but provided double
    "aShort = 1 - 1l, 0xA0179", // expected short but provided long
    "aShort = 1l - 1, 0xA0179", // expected short but provided long
    "aShort = 1 - 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1f - 1, 0xA0179", // expected short but provided float
    "aShort = 1 - 0.1, 0xA0179", // expected short but provided double
    "aShort = 0.1 - 1, 0xA0179", // expected short but provided double
    "aShort = 1 * 1l, 0xA0179", // expected short but provided long
    "aShort = 1l * 1, 0xA0179", // expected short but provided long
    "aShort = 1 * 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1f * 1, 0xA0179", // expected short but provided float
    "aShort = 1 * 0.1, 0xA0179", // expected short but provided double
    "aShort = 0.1 * 1, 0xA0179", // expected short but provided double
    "aShort = 1 / 1l, 0xA0179", // expected short but provided long
    "aShort = 1l / 1, 0xA0179", // expected short but provided long
    "aShort = 1 / 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1f / 1, 0xA0179", // expected short but provided float
    "aShort = 1 / 0.1, 0xA0179", // expected short but provided double
    "aShort = 0.1 / 1, 0xA0179", // expected short but provided double
    "aShort = 1 % 1l, 0xA0179", // expected short but provided long
    "aShort = 1l % 1, 0xA0179", // expected short but provided long
    "aShort = 1 % 0.1f, 0xA0179", // expected short but provided float
    "aShort = 0.1f % 1, 0xA0179", // expected short but provided float
    "aShort = 1 % 0.1, 0xA0179", // expected short but provided double
    "aShort = 0.1 % 1, 0xA0179", // expected short but provided double
    "anInt = 1 + 1l, 0xA0179", // expected int but provided long
    "anInt = 1l + 1, 0xA0179", // expected int but provided long
    "anInt = 1 + 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1f + 1, 0xA0179", // expected int but provided float
    "anInt = 1 + 0.1, 0xA0179", // expected int but provided double
    "anInt = 0.1 + 1, 0xA0179", // expected int but provided double
    "anInt = 1 - 1l, 0xA0179", // expected int but provided long
    "anInt = 1l - 1, 0xA0179", // expected int but provided long
    "anInt = 1 - 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1f - 1, 0xA0179", // expected int but provided float
    "anInt = 1 - 0.1, 0xA0179", // expected int but provided double
    "anInt = 0.1 - 1, 0xA0179", // expected int but provided double
    "anInt = 1 * 1l, 0xA0179", // expected int but provided long
    "anInt = 1l * 1, 0xA0179", // expected int but provided long
    "anInt = 1 * 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1f * 1, 0xA0179", // expected int but provided float
    "anInt = 1 * 0.1, 0xA0179", // expected int but provided double
    "anInt = 0.1 * 1, 0xA0179", // expected int but provided double
    "anInt = 1 / 1l, 0xA0179", // expected int but provided long
    "anInt = 1l / 1, 0xA0179", // expected int but provided long
    "anInt = 1 / 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1f / 1, 0xA0179", // expected int but provided float
    "anInt = 1 / 0.1, 0xA0179", // expected int but provided double
    "anInt = 0.1 / 1, 0xA0179", // expected int but provided double
    "anInt = 1 % 1l, 0xA0179", // expected int but provided long
    "anInt = 1l % 1, 0xA0179", // expected int but provided long
    "anInt = 1 % 0.1f, 0xA0179", // expected int but provided float
    "anInt = 0.1f % 1, 0xA0179", // expected int but provided float
    "anInt = 1 % 0.1, 0xA0179", // expected int but provided double
    "anInt = 0.1 % 1, 0xA0179", // expected int but provided double
    "aLong = 1l + 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1f + 1l, 0xA0179", // expected long but provided float
    "aLong = 1l + 0.1, 0xA0179", // expected long but provided double
    "aLong = 0.1 + 1l, 0xA0179", // expected long but provided double
    "aLong = 1l - 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1f - 1l, 0xA0179", // expected long but provided float
    "aLong = 1l - 0.1, 0xA0179", // expected long but provided double
    "aLong = 0.1 - 1l, 0xA0179", // expected long but provided double
    "aLong = 1l * 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1f * 1l, 0xA0179", // expected long but provided float
    "aLong = 1l * 0.1, 0xA0179", // expected long but provided double
    "aLong = 0.1 * 1l, 0xA0179", // expected long but provided double
    "aLong = 1l / 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1f / 1l, 0xA0179", // expected long but provided float
    "aLong = 1l / 0.1, 0xA0179", // expected long but provided double
    "aLong = 0.1 / 1l, 0xA0179", // expected long but provided double
    "aLong = 1l % 0.1f, 0xA0179", // expected long but provided float
    "aLong = 0.1f % 1l, 0xA0179", // expected long but provided float
    "aLong = 1l % 0.1, 0xA0179", // expected long but provided double
    "aLong = 0.1 % 1l, 0xA0179", // expected long but provided double
    "aFloat = 0.1f + 0.1, 0xA0179", // expected float but provided double
    "aFloat = 0.1 + 0.1f, 0xA0179", // expected float but provided double
    "aFloat = 0.1f - 0.1, 0xA0179", // expected float but provided double
    "aFloat = 0.1 - 0.1f, 0xA0179", // expected float but provided double
    "aFloat = 0.1f * 0.1, 0xA0179", // expected float but provided double
    "aFloat = 0.1 * 0.1f, 0xA0179", // expected float but provided double
    "aFloat = 0.1f / 0.1, 0xA0179", // expected float but provided double
    "aFloat = 0.1 / 0.1f, 0xA0179", // expected float but provided double
    "aFloat = 0.1f % 0.1, 0xA0179", // expected float but provided double
    "aFloat = 0.1 % 0.1f, 0xA0179", // expected float but provided double
    "aBoolean && aChar, 0xB0113", // && not applicable to boolean, char
    "aBoolean && aByte, 0xB0113", // && not applicable to boolean, byte
    "aBoolean && aShort, 0xB0113", // && not applicable to boolean, short
    "aBoolean && anInt, 0xB0113", // && not applicable to boolean, int
    "aBoolean && aLong, 0xB0113", // && not applicable to boolean, long
    "aBoolean && aFloat, 0xB0113", // && not applicable to boolean, float
    "aBoolean && aDouble, 0xB0113", // && not applicable to boolean, double
    "aChar && aBoolean, 0xB0113", // && not applicable to char, boolean
    "aChar && aChar, 0xB0113", // && not applicable to char, char
    "aChar && aByte, 0xB0113", // && not applicable to char, byte
    "aChar && aShort, 0xB0113", // && not applicable to char, short
    "aChar && anInt, 0xB0113", // && not applicable to char, int
    "aChar && aLong, 0xB0113", // && not applicable to char, long
    "aChar && aFloat, 0xB0113", // && not applicable to char, float
    "aChar && aDouble, 0xB0113", // && not applicable to char, double
    "aByte && aBoolean, 0xB0113", // && not applicable to byte, boolean
    "aByte && aChar, 0xB0113", // && not applicable to byte, char
    "aByte && aByte, 0xB0113", // && not applicable to byte, byte
    "aByte && aShort, 0xB0113", // && not applicable to byte, short
    "aByte && anInt, 0xB0113", // && not applicable to byte, int
    "aByte && aLong, 0xB0113", // && not applicable to byte, long
    "aByte && aFloat, 0xB0113", // && not applicable to byte, float
    "aByte && aDouble, 0xB0113", // && not applicable to byte, double
    "aShort && aBoolean, 0xB0113", // && not applicable to short, boolean
    "aShort && aChar, 0xB0113", // && not applicable to short, char
    "aShort && aByte, 0xB0113", // && not applicable to short, byte
    "aShort && aShort, 0xB0113", // && not applicable to short, short
    "aShort && anInt, 0xB0113", // && not applicable to short, int
    "aShort && aLong, 0xB0113", // && not applicable to short, long
    "aShort && aFloat, 0xB0113", // && not applicable to short, float
    "aShort && aDouble, 0xB0113", // && not applicable to short, double
    "anInt && aBoolean, 0xB0113", // && not applicable to short, boolean
    "anInt && aChar, 0xB0113", // && not applicable to int, char
    "anInt && aByte, 0xB0113", // && not applicable to int, byte
    "anInt && aShort, 0xB0113", // && not applicable to int, short
    "anInt && anInt, 0xB0113", // && not applicable to int, int
    "anInt && aLong, 0xB0113", // && not applicable to int, long
    "anInt && aFloat, 0xB0113", // && not applicable to int, float
    "anInt && aDouble, 0xB0113", // && not applicable to int, double
    "aLong && aBoolean, 0xB0113", // && not applicable to long, boolean
    "aLong && aChar, 0xB0113", // && not applicable to long, char
    "aLong && aByte, 0xB0113", // && not applicable to long, byte
    "aLong && aShort, 0xB0113", // && not applicable to long, short
    "aLong && anInt, 0xB0113", // && not applicable to long, int
    "aLong && aLong, 0xB0113", // && not applicable to long, long
    "aLong && aFloat, 0xB0113", // && not applicable to long, float
    "aLong && aDouble, 0xB0113", // && not applicable to long, double
    "aFloat && aBoolean, 0xB0113", // && not applicable to float, boolean
    "aFloat && aChar, 0xB0113", // && not applicable to float, char
    "aFloat && aByte, 0xB0113", // && not applicable to float, byte
    "aFloat && aShort, 0xB0113", // && not applicable to float, short
    "aFloat && anInt, 0xB0113", // && not applicable to float, int
    "aFloat && aLong, 0xB0113", // && not applicable to float, long
    "aFloat && aFloat, 0xB0113", // && not applicable to float, float
    "aFloat && aDouble, 0xB0113", // && not applicable to float, double
    "aDouble && aBoolean, 0xB0113", // && not applicable to double, boolean
    "aDouble && aChar, 0xB0113", // && not applicable to double, char
    "aDouble && aByte, 0xB0113", // && not applicable to double, byte
    "aDouble && aShort, 0xB0113", // && not applicable to double, short
    "aDouble && anInt, 0xB0113", // && not applicable to double, int
    "aDouble && aLong, 0xB0113", // && not applicable to double, long
    "aDouble && aFloat, 0xB0113", // && not applicable to double, float
    "aDouble && aDouble, 0xB0113", // && not applicable to double, double
    "aBoolean || aChar, 0xB0113", // || not applicable to boolean, char
    "aBoolean || aByte, 0xB0113", // || not applicable to boolean, byte
    "aBoolean || aShort, 0xB0113", // || not applicable to boolean, short
    "aBoolean || anInt, 0xB0113", // || not applicable to boolean, int
    "aBoolean || aLong, 0xB0113", // || not applicable to boolean, long
    "aBoolean || aFloat, 0xB0113", // || not applicable to boolean, float
    "aBoolean || aDouble, 0xB0113", // || not applicable to boolean, double
    "aChar || aBoolean, 0xB0113", // || not applicable to char, boolean
    "aChar || aChar, 0xB0113", // || not applicable to char, char
    "aChar || aByte, 0xB0113", // || not applicable to char, byte
    "aChar || aShort, 0xB0113", // || not applicable to char, short
    "aChar || anInt, 0xB0113", // || not applicable to char, int
    "aChar || aLong, 0xB0113", // || not applicable to char, long
    "aChar || aFloat, 0xB0113", // || not applicable to char, float
    "aChar || aDouble, 0xB0113", // || not applicable to char, double
    "aByte || aBoolean, 0xB0113", // || not applicable to byte, boolean
    "aByte || aChar, 0xB0113", // || not applicable to byte, char
    "aByte || aByte, 0xB0113", // || not applicable to byte, byte
    "aByte || aShort, 0xB0113", // || not applicable to byte, short
    "aByte || anInt, 0xB0113", // || not applicable to byte, int
    "aByte || aLong, 0xB0113", // || not applicable to byte, long
    "aByte || aFloat, 0xB0113", // || not applicable to byte, float
    "aByte || aDouble, 0xB0113", // || not applicable to byte, double
    "aShort || aBoolean, 0xB0113", // || not applicable to short, boolean
    "aShort || aChar, 0xB0113", // || not applicable to short, char
    "aShort || aByte, 0xB0113", // || not applicable to short, byte
    "aShort || aShort, 0xB0113", // || not applicable to short, short
    "aShort || anInt, 0xB0113", // || not applicable to short, int
    "aShort || aLong, 0xB0113", // || not applicable to short, long
    "aShort || aFloat, 0xB0113", // || not applicable to short, float
    "aShort || aDouble, 0xB0113", // || not applicable to short, double
    "anInt || aBoolean, 0xB0113", // || not applicable to short, boolean
    "anInt || aChar, 0xB0113", // || not applicable to int, char
    "anInt || aByte, 0xB0113", // || not applicable to int, byte
    "anInt || aShort, 0xB0113", // || not applicable to int, short
    "anInt || anInt, 0xB0113", // || not applicable to int, int
    "anInt || aLong, 0xB0113", // || not applicable to int, long
    "anInt || aFloat, 0xB0113", // || not applicable to int, float
    "anInt || aDouble, 0xB0113", // || not applicable to int, double
    "aLong || aBoolean, 0xB0113", // || not applicable to long, boolean
    "aLong || aChar, 0xB0113", // || not applicable to long, char
    "aLong || aByte, 0xB0113", // || not applicable to long, byte
    "aLong || aShort, 0xB0113", // || not applicable to long, short
    "aLong || anInt, 0xB0113", // || not applicable to long, int
    "aLong || aLong, 0xB0113", // || not applicable to long, long
    "aLong || aFloat, 0xB0113", // || not applicable to long, float
    "aLong || aDouble, 0xB0113", // || not applicable to long, double
    "aFloat || aBoolean, 0xB0113", // || not applicable to float, boolean
    "aFloat || aChar, 0xB0113", // || not applicable to float, char
    "aFloat || aByte, 0xB0113", // || not applicable to float, byte
    "aFloat || aShort, 0xB0113", // || not applicable to float, short
    "aFloat || anInt, 0xB0113", // || not applicable to float, int
    "aFloat || aLong, 0xB0113", // || not applicable to float, long
    "aFloat || aFloat, 0xB0113", // || not applicable to float, float
    "aFloat || aDouble, 0xB0113", // || not applicable to float, double
    "aDouble || aBoolean, 0xB0113", // || not applicable to double, boolean
    "aDouble || aChar, 0xB0113", // || not applicable to double, char
    "aDouble || aByte, 0xB0113", // || not applicable to double, byte
    "aDouble || aShort, 0xB0113", // || not applicable to double, short
    "aDouble || anInt, 0xB0113", // || not applicable to double, int
    "aDouble || aLong, 0xB0113", // || not applicable to double, long
    "aDouble || aFloat, 0xB0113", // || not applicable to double, float
    "aDouble || aDouble, 0xB0113", // || not applicable to double, double
    "aBoolean > aChar, 0xB0167", // > not applicable to boolean, char
    "aBoolean > aByte, 0xB0167", // > not applicable to boolean, byte
    "aBoolean > aShort, 0xB0167", // > not applicable to boolean, short
    "aBoolean > anInt, 0xB0167", // > not applicable to boolean, int
    "aBoolean > aLong, 0xB0167", // > not applicable to boolean, long
    "aBoolean > aFloat, 0xB0167", // > not applicable to boolean, float
    "aBoolean > aDouble, 0xB0167", // > not applicable to boolean, double
    "aChar > aBoolean, 0xB0167", // > not applicable to char, boolean
    "aByte > aBoolean, 0xB0167", // > not applicable to byte, boolean
    "aShort > aBoolean, 0xB0167", // > not applicable to short, boolean
    "anInt > aBoolean, 0xB0167", // > not applicable to int, boolean
    "aLong > aBoolean, 0xB0167", // > not applicable to long, boolean
    "aFloat > aBoolean, 0xB0167", // > not applicable to float, boolean
    "aDouble > aBoolean, 0xB0167", // > not applicable to double, boolean
    "aBoolean < aChar, 0xB0167", // < not applicable to boolean, char
    "aBoolean < aByte, 0xB0167", // < not applicable to boolean, byte
    "aBoolean < aShort, 0xB0167", // < not applicable to boolean, short
    "aBoolean < anInt, 0xB0167", // < not applicable to boolean, int
    "aBoolean < aLong, 0xB0167", // < not applicable to boolean, long
    "aBoolean < aFloat, 0xB0167", // < not applicable to boolean, float
    "aBoolean < aDouble, 0xB0167", // < not applicable to boolean, double
    "aChar < aBoolean, 0xB0167", // < not applicable to char, boolean
    "aByte < aBoolean, 0xB0167", // < not applicable to byte, boolean
    "aShort < aBoolean, 0xB0167", // < not applicable to short, boolean
    "anInt < aBoolean, 0xB0167", // < not applicable to int, boolean
    "aLong < aBoolean, 0xB0167", // < not applicable to long, boolean
    "aFloat < aBoolean, 0xB0167", // < not applicable to float, boolean
    "aDouble < aBoolean, 0xB0167", // < not applicable to double, boolean
    "aBoolean >= aChar, 0xB0167", // >= not applicable to boolean, char
    "aBoolean >= aByte, 0xB0167", // >= not applicable to boolean, byte
    "aBoolean >= aShort, 0xB0167", // >= not applicable to boolean, short
    "aBoolean >= anInt, 0xB0167", // >= not applicable to boolean, int
    "aBoolean >= aLong, 0xB0167", // >= not applicable to boolean, long
    "aBoolean >= aFloat, 0xB0167", // >= not applicable to boolean, float
    "aBoolean >= aDouble, 0xB0167", // >= not applicable to boolean, double
    "aChar >= aBoolean, 0xB0167", // >= not applicable to char, boolean
    "aByte >= aBoolean, 0xB0167", // >= not applicable to byte, boolean
    "aShort >= aBoolean, 0xB0167", // >= not applicable to short, boolean
    "anInt >= aBoolean, 0xB0167", // >= not applicable to int, boolean
    "aLong >= aBoolean, 0xB0167", // >= not applicable to long, boolean
    "aFloat >= aBoolean, 0xB0167", // >= not applicable to float, boolean
    "aDouble >= aBoolean, 0xB0167", // >= not applicable to double, boolean
    "aBoolean <= aChar, 0xB0167", // <= not applicable to boolean, char
    "aBoolean <= aByte, 0xB0167", // <= not applicable to boolean, byte
    "aBoolean <= aShort, 0xB0167", // <= not applicable to boolean, short
    "aBoolean <= anInt, 0xB0167", // <= not applicable to boolean, int
    "aBoolean <= aLong, 0xB0167", // <= not applicable to boolean, long
    "aBoolean <= aFloat, 0xB0167", // <= not applicable to boolean, float
    "aBoolean <= aDouble, 0xB0167", // <= not applicable to boolean, double
    "aChar <= aBoolean, 0xB0167", // <= not applicable to char, boolean
    "aByte <= aBoolean, 0xB0167", // <= not applicable to byte, boolean
    "aShort <= aBoolean, 0xB0167", // <= not applicable to short, boolean
    "anInt <= aBoolean, 0xB0167", // <= not applicable to int, boolean
    "aLong <= aBoolean, 0xB0167", // <= not applicable to long, boolean
    "aFloat <= aBoolean, 0xB0167", // <= not applicable to float, boolean
    "aDouble <= aBoolean, 0xB0167", // <= not applicable to double, boolean
    "aBoolean == aChar, 0xB0166", // == not applicable to boolean, char
    "aBoolean == aByte, 0xB0166", // == not applicable to boolean, byte
    "aBoolean == aShort, 0xB0166", // == not applicable to boolean, short
    "aBoolean == anInt, 0xB0166", // == not applicable to boolean, int
    "aBoolean == aLong, 0xB0166", // == not applicable to boolean, long
    "aBoolean == aFloat, 0xB0166", // == not applicable to boolean, float
    "aBoolean == aDouble, 0xB0166", // == not applicable to boolean, double
    "aChar == aBoolean, 0xB0166", // == not applicable to char, boolean
    "aByte == aBoolean, 0xB0166", // == not applicable to byte, boolean
    "aShort == aBoolean, 0xB0166", // == not applicable to short, boolean
    "anInt == aBoolean, 0xB0166", // == not applicable to int, boolean
    "aLong == aBoolean, 0xB0166", // == not applicable to long, boolean
    "aFloat == aBoolean, 0xB0166", // == not applicable to float, boolean
    "aDouble == aBoolean, 0xB0166", // == not applicable to double, boolean
    "aBoolean != aChar, 0xB0166", // != not applicable to boolean, char
    "aBoolean != aByte, 0xB0166", // != not applicable to boolean, byte
    "aBoolean != aShort, 0xB0166", // != not applicable to boolean, short
    "aBoolean != anInt, 0xB0166", // != not applicable to boolean, int
    "aBoolean != aLong, 0xB0166", // != not applicable to boolean, long
    "aBoolean != aFloat, 0xB0166", // != not applicable to boolean, float
    "aBoolean != aDouble, 0xB0166", // != not applicable to boolean, double
    "aChar != aBoolean, 0xB0166", // != not applicable to char, boolean
    "aByte != aBoolean, 0xB0166", // != not applicable to byte, boolean
    "aShort != aBoolean, 0xB0166", // != not applicable to short, boolean
    "anInt != aBoolean, 0xB0166", // != not applicable to int, boolean
    "aLong != aBoolean, 0xB0166", // != not applicable to long, boolean
    "aFloat != aBoolean, 0xB0166", // != not applicable to float, boolean
    "aDouble != aBoolean, 0xB0166", // != not applicable to double, boolean
    "aChar = aBoolean && aBoolean, 0xA0179", // expected char but provided boolean
    "aByte = aBoolean && aBoolean, 0xA0179", // expected byte but provided boolean
    "aShort = aBoolean && aBoolean, 0xA0179", // expected short but provided boolean
    "anInt = aBoolean && aBoolean, 0xA0179", // expected int but provided boolean
    "aLong = aBoolean && aBoolean, 0xA0179", // expected long but provided boolean
    "aFloat = aBoolean && aBoolean, 0xA0179", // expected float but provided boolean
    "aDouble = aBoolean && aBoolean, 0xA0179", // expected double but provided boolean
    "aChar = aBoolean || aBoolean, 0xA0179", // expected char but provided boolean
    "aByte = aBoolean || aBoolean, 0xA0179", // expected byte but provided boolean
    "aShort = aBoolean || aBoolean, 0xA0179", // expected short but provided boolean
    "anInt = aBoolean || aBoolean, 0xA0179", // expected int but provided boolean
    "aLong = aBoolean || aBoolean, 0xA0179", // expected long but provided boolean
    "aFloat = aBoolean || aBoolean, 0xA0179", // expected float but provided boolean
    "aDouble = aBoolean || aBoolean, 0xA0179", // expected double but provided boolean
    "aChar = aChar > aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte > aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort > aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt > anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong > aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat > aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble > aDouble, 0xA0179", // expected double but provided boolean
    "aChar = aChar < aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte < aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort < aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt < anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong < aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat < aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble < aDouble, 0xA0179", // expected double but provided boolean
    "aChar = aChar >= aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte >= aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort >= aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt >= anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong >= aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat >= aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble >= aDouble, 0xA0179", // expected double but provided boolean
    "aChar = aChar <= aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte <= aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort <= aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt <= anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong <= aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat <= aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble <= aDouble, 0xA0179", // expected double but provided boolean
    "aChar = aChar == aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte == aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort == aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt == anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong == aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat == aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble == aDouble, 0xA0179", // expected double but provided boolean
    "aChar = aChar != aChar, 0xA0179", // expected char but provided boolean
    "aByte = aByte != aByte, 0xA0179", // expected byte but provided boolean
    "aShort = aShort != aShort, 0xA0179", // expected short but provided boolean
    "anInt = anInt != anInt, 0xA0179", // expected int but provided boolean
    "aLong = aLong != aLong, 0xA0179", // expected long but provided boolean
    "aFloat = aFloat != aFloat, 0xA0179", // expected float but provided boolean
    "aDouble = aDouble != aDouble, 0xA0179", // expected double but provided boolean
    "aByte ? 0 : 1, 0xB0165", // ? not applicable to byte
    "aShort ? 0 : 1, 0xB0165", // ? not applicable to short
    "aChar ? 0 : 1, 0xB0165", // ? not applicable to char
    "anInt ? 0 : 1, 0xB0165", // ? not applicable to int
    "aLong ? 0 : 1, 0xB0165", // ? not applicable to long
    "aFloat ? 0 : 1, 0xB0165", // ? not applicable to float
    "aDouble ? 0 : 1, 0xB0165", // ? not applicable to double
    "aBoolean = aBoolean ? aByte : aBoolean, 0xA0179", // expected boolean but provided (byte, boolean)
    "aBoolean = aBoolean ? aBoolean : aByte, 0xA0179", // expected boolean but provided (boolean, byte)
    "aBoolean = aBoolean ? aByte : aByte, 0xA0179", // expected boolean but provided (byte, byte)
  })
  public void testInvalidExpression(@NotNull String expr,
                                    @NotNull String error) throws IOException {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(error);
    Preconditions.checkArgument(!expr.isBlank());
    Preconditions.checkArgument(!error.isBlank());

    MontiArcTypeCalculator tc = new MontiArcTypeCalculator();
    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    tc.deriveType(ast).getResult();

    // Then
    Assertions.assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings()))
      .as("Expression " + expr + " is invalid, the findings should contain exactly the expected error.")
      .containsExactly(error);
  }
}
