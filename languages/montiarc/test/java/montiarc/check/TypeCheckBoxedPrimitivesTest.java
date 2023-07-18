/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
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
import java.util.List;

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
 * This class provides tests for validating the correctness of encapsulating
 * primitives in boxing types.
 * <p>
 * The class under test is {@link MontiArcTypeCalculator}.
 */
public class TypeCheckBoxedPrimitivesTest extends MontiArcAbstractTest {

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
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
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
    FieldSymbol anObject = MontiArcMill.fieldSymbolBuilder()
      .setName("anObject")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Object", this.scope))
      .build();
    SymbolService.link(this.scope, anObject);
    FieldSymbol aSerializable = MontiArcMill.fieldSymbolBuilder()
      .setName("aSerializable")
      .setType(SymTypeExpressionFactory.createTypeObject("java.io.Serializable", this.scope))
      .build();
    SymbolService.link(this.scope, aSerializable);
    FieldSymbol aWBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aWBoolean")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Boolean", this.scope))
      .build();
    SymbolService.link(this.scope, aWBoolean);
    FieldSymbol aWChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aWChar")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Character", this.scope))
      .build();
    SymbolService.link(this.scope, aWChar);
    FieldSymbol aNumber = MontiArcMill.fieldSymbolBuilder()
      .setName("aNumber")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Number", this.scope))
      .build();
    SymbolService.link(this.scope, aNumber);
    FieldSymbol aWByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aWByte")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Byte", this.scope))
      .build();
    SymbolService.link(this.scope, aWByte);
    FieldSymbol aWShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aWShort")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Short", this.scope))
      .build();
    SymbolService.link(this.scope, aWShort);
    FieldSymbol aWInt = MontiArcMill.fieldSymbolBuilder()
      .setName("aWInt")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Integer", this.scope))
      .build();
    SymbolService.link(this.scope, aWInt);
    FieldSymbol aWLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aWLong")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Long", this.scope))
      .build();
    SymbolService.link(this.scope, aWLong);
    FieldSymbol aWFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aWFloat")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Float", this.scope))
      .build();
    SymbolService.link(this.scope, aWFloat);
    FieldSymbol aWDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aWDouble")
      .setType(SymTypeExpressionFactory.createTypeObject("java.lang.Double", this.scope))
      .build();
    SymbolService.link(this.scope, aWDouble);
    FieldSymbol aCpaBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaBoolean")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Boolean", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaBoolean);
    FieldSymbol aCpaChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaChar")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Character", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaChar);
    FieldSymbol aCpaByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaByte")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Byte", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaByte);
    FieldSymbol aCpaShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaShort")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Short", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaShort);
    FieldSymbol aCpaInt = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaInt")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Integer", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaInt);
    FieldSymbol aCpaLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaLong")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Long", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaLong);
    FieldSymbol aCpaFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaFloat")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Float", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaFloat);
    FieldSymbol aCpaDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aCpaDouble")
      .setType(SymTypeExpressionFactory.createGenerics("java.lang.Comparable", this.scope,
        List.of(SymTypeExpressionFactory.createTypeObject("java.lang.Double", this.scope))))
      .build();
    SymbolService.link(this.scope, aCpaDouble);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "aWBoolean = true", // box boolean in Boolean (true literal)
    "aWBoolean = false", // box boolean in Boolean (false literal)
    "aWBoolean = aBoolean", // box boolean in Boolean (boolean variable)
    "aWBoolean = aWBoolean", // assign Boolean to Boolean (variable)
    "aWChar = 'a'", // box char in Character (literal)
    //"aWChar = 0", // cast int to char, box char in Character (min value)
    //"aWChar = 65535", //  cast int to char, box char in Character (max value)
    "aWChar = aChar", // box char in Character (variable)
    "aWChar = aWChar", // expected Character, provided Character (variable)
    //"aWByte = 'a'", // cast char to byte, box byte in Byte (min value)
    //"aWByte = 'u007F'", // cast char to byte, box byte in Byte (max value)
    //"aWByte = 0", // cast int to byte, box byte in Byte (literal)
    //"aWByte = +1", // cast int to byte, box byte in Byte (signed literal)
    //"aWByte = -1", // cast int to byte, box byte in Byte (signed literal)
    //"aWByte = 127", // cast int to byte, box byte in Byte (max value)
    //"aWByte = -128", // cast int to byte, box byte in Byte (min value)
    "aWByte = aByte", // box byte in Byte (variable)
    "aWByte = aWByte", // assign Byte to Byte (variable)
    //"aWShort = 'a'", // cast char to short, box short in Short (min value)
    //"aWShort = 0", // cast int to short, box short in Short (literal)
    //"aWShort = +1", // cast int to short, box short in Short (signed literal)
    //"aWShort = -1", // cast int to short, box short in Short (signed literal)
    //"aWShort = 32767", // cast int to short, box short in Short (max value)
    //"aWShort = -32768", // cast int to short, box short in Short (min value)
    "aWShort = aByte", // cast byte to short, box short in Short (variable)
    "aWShort = aShort", // box short in Short (variable)
    "aWShort = aWByte", // unbox Byte, cast byte to short, box short in Short (variable)
    "aWShort = aWShort", // assign Short to Short (variable)
    "aWInt = 'a'", // cast char to int, box int in Integer (min value)
    "aWInt = 0", // box int in Integer (literal)
    "aWInt = +1", // box int in Integer (signed literal)
    "aWInt = -1", // box int in Integer (signed literal)
    "aWInt = 2147483647", // box int in Integer (max value)
    "aWInt = -2147483648", // box int in Integer (min value)
    "aWInt = aChar", // cast char to int, box int in Integer (variable)
    "aWInt = aByte", // cast byte to int, box int in Integer (variable)
    "aWInt = aShort", // cast short to int, box int in Integer (variable)
    "aWInt = anInt", // box int in Integer (variable)
    "aWInt = aWChar", // unbox Character, cast char to int, box int in Integer (variable)
    "aWInt = aWByte", // unbox Byte, cast byte to int, box int in Integer (variable)
    "aWInt = aWShort", // unbox Short, cast short to int, box int in Integer (variable)
    "aWInt = aWInt", // expected Integer, provided Integer (variable)
    "aWLong = 'a'", // cast char to long, box long in Long (min value)
    "aWLong = 0", // cast int to long, box long in Long (literal)
    "aWLong = 0l", // box long in Long (literal)
    "aWLong = +1l", // box long in Long (signed literal)
    "aWLong = -1l", // box long in Long (signed literal)
    "aWLong = 9223372036854775807l", // box long in Long (max value)
    "aWLong = -9223372036854775808l", // box long in Long (min value)
    "aWLong = aChar", // cast char to long, box long in Long (variable)
    "aWLong = aByte", // cast byte to long, box long in Long (variable)
    "aWLong = aShort", // cast short to long, box long in Long (variable)
    "aWLong = anInt", // cast int to long, box long in Long (variable)
    "aWLong = aLong", // box long in Long (variable)
    "aWLong = aWChar", // unbox Character, cast char to long, box long in Long (variable)
    "aWLong = aWByte", // unbox Byte, cast byte to long, box long in Long (variable)
    "aWLong = aWShort", // unbox Short, cast short to long, box long in Long (variable)
    "aWLong = aWInt", // unbox Integer, cast int to long, box long in Long (variable)
    "aWLong = aWLong", // expected Long, provided Long (variable)
    "aWFloat = 'a'", // cast char to float, box float in Float (min value)
    "aWFloat = 0", // cast int to float, box float in Float (literal)
    "aWFloat = 0l", // cast long to float, box float in Float (literal)
    //"aWFloat = 0f", // box float in Float (literal)
    //"aWFloat = 0.f", // box float in Float (literal)
    "aWFloat = 0.0f", // box float in Float (literal)
    "aWFloat = +0.1f", // box float in Float (signed literal)
    "aWFloat = -0.1f", // box float in Float (signed literal)
    "aWFloat = aChar", // cast char to float, box float in Float (variable)
    "aWFloat = aByte", // cast byte to float, box float in Float (variable)
    "aWFloat = aShort", // cast short to float, box float in Float (variable)
    "aWFloat = anInt", // cast int to float, box float in Float (variable)
    "aWFloat = aLong", // cast long to float, box float in Float (variable)
    "aWFloat = aFloat", // box float in Float (variable)
    "aWFloat = aWChar", // unbox Character, cast char to float, box float in Float (variable)
    "aWFloat = aWByte", // unbox Byte, cast byte to float, box float in Float (variable)
    "aWFloat = aWShort", // unbox Short, cast short to float, box float in Float (variable)
    "aWFloat = aWInt", // unbox Integer, cast int to float, box float in Float (variable)
    "aWFloat = aWLong", // unbox Long, cast long to float, box float in Float (variable)
    "aWFloat = aWFloat", // assign Float to Float (variable)
    "aWDouble = 'a'", // cast char to double, box double in Double (min value)
    "aWDouble = 0", // cast int to double, box double in Double (literal)
    "aWDouble = 0l", // cast long to double, box double in Double (literal)
    "aWDouble = 0.0f", // cast float to double, box double in Double (literal)
    "aWDouble = 0.0", // box double in Double (literal)
    "aWDouble = +0.1", // box double in Double (signed literal)
    "aWDouble = -0.1", // box double in Double (signed literal)
    "aWDouble = aChar", // cast char to double, box double in Double (variable)
    "aWDouble = aByte", // cast byte to double, box double in Double (variable)
    "aWDouble = aShort", // cast short to double, box double in Double (variable)
    "aWDouble = anInt", // cast int to double, box double in Double (variable)
    "aWDouble = aLong", // cast long to double, box double in Double (variable)
    "aWDouble = aFloat", // cast float to double, box double in Double (variable)
    "aWDouble = aDouble", // box double in Double (variable)
    "aWDouble = aWChar", // unbox Character, cast char to double, box double in Double (variable)
    "aWDouble = aWByte", // unbox Byte, cast byte to double, box double in Double (variable)
    "aWDouble = aWShort", // unbox Short, cast short to double, box double in Double (variable)
    "aWDouble = aWInt", // unbox Integer, cast int to double, box double in Double (variable)
    "aWDouble = aWLong", // unbox Long, cast long to double, box double in Double (variable)
    "aWDouble = aWFloat", // unbox Float, cast float to double, box double in Double (variable)
    "aWDouble = aWDouble", // assign Double to Double (variable)
    "anObject = true", // box boolean in Boolean, assign to Object (true literal)
    "anObject = false", // box boolean in Boolean, assign to Object (false literal)
    "anObject = 'a'", // box char in Character, assign to Object (literal)
    "anObject = 0", // box int in Integer, assign to Object (literal)
    "anObject = +1", // box int in Integer, assign to Object (signed literal)
    "anObject = -1", // box int in Integer, assign to Object (signed literal)
    "anObject = 0l", // box long in Long, assign to Object (literal)
    "anObject = +1l", // box long in Long, assign to Object (signed literal)
    "anObject = -1l", // box long in Long, assign to Object (signed literal)
    "anObject = 0.0f", // box float in Float, assign to Object (literal)
    "anObject = +0.1f", // box float in Float, assign to Object (signed literal)
    "anObject = -0.1f", // box float in Float, assign to Object (signed literal)
    "anObject = 0.0", // box double in Double, assign to Object (literal)
    "anObject = +0.1", // box double in Double, assign to Object (signed literal)
    "anObject = -0.1", // box double in Double, assign to Object (signed literal)a
    "anObject = aWBoolean", // assign Character to Object (variable)
    "anObject = aWChar", // assign Character to Object (variable)
    "anObject = aWByte", // assign Byte to Object (variable)
    "anObject = aWShort", // assign Short to Object (variable)
    "anObject = aWInt", // assign Integer to Object (variable)
    "anObject = aWLong", // assign Long to Object (variable)
    "anObject = aWFloat", // assign Float to Object (variable)
    "anObject = aWDouble", // assign Double to Object (variable)
    "aSerializable = true", // box boolean in Boolean, assign to Serializable (true literal)
    "aSerializable = false", // box boolean in Boolean, assign to Serializable (false literal)
    "aSerializable = 'a'", // box char in Character, assign to Serializable (literal)
    "aSerializable = 0", // box int in Integer, assign to Serializable (literal)
    "aSerializable = +1", // box int in Integer, assign to Serializable (signed literal)
    "aSerializable = -1", // box int in Integer, assign to Serializable (signed literal)
    "aSerializable = 0l", // box long in Long, assign to Serializable (literal)
    "aSerializable = +1l", // box long in Long, assign to Serializable (signed literal)
    "aSerializable = -1l", // box long in Long, assign to Serializable (signed literal)
    "aSerializable = 0.0f", // box float in Float, assign to Serializable (literal)
    "aSerializable = +0.1f", // box float in Float, assign to Serializable (signed literal)
    "aSerializable = -0.1f", // box float in Float, assign to Serializable (signed literal)
    "aSerializable = 0.0", // box double in Double, assign to Serializable (literal)
    "aSerializable = +0.1", // box double in Double, assign to Serializable (signed literal)
    "aSerializable = -0.1", // box double in Double, assign to Serializable (signed literal)a
    "aSerializable = aWBoolean", // assign Character to Serializable (variable)
    "aSerializable = aWChar", // assign Character to Serializable (variable)
    "aSerializable = aWByte", // assign Byte to Serializable (variable)
    "aSerializable = aWShort", // assign Short to Serializable (variable)
    "aSerializable = aWInt", // assign Integer to Serializable (variable)
    "aSerializable = aWLong", // assign Long to Serializable (variable)
    "aSerializable = aWFloat", // assign Float to Serializable (variable)
    "aSerializable = aWDouble", // assign Double to Serializable (variable)
    "aNumber = 0", // box int in Integer, assign to Number (literal)
    "aNumber = +1", // box int in Integer, assign to Number (signed literal)
    "aNumber = -1", // box int in Integer, assign to Number (signed literal)
    "aNumber = 0l", // box long in Long, assign to Number (literal)
    "aNumber = +1l", // box long in Long, assign to Number (signed literal)
    "aNumber = -1l", // box long in Long, assign to Number (signed literal)
    "aNumber = 0.0f", // box float in Float, assign to Number (literal)
    "aNumber = +0.1f", // box float in Float, assign to Number (signed literal)
    "aNumber = -0.1f", // box float in Float, assign to Number (signed literal)
    "aNumber = 0.0", // box double in Double, assign to Number (literal)
    "aNumber = +0.1", // box double in Double, assign to Number (signed literal)
    "aNumber = -0.1", // box double in Double, assign to Number (signed literal)
    "aNumber = aWByte", // assign Byte to Number (variable)
    "aNumber = aWShort", // assign Short to Number (variable)
    "aNumber = aWInt", // assign Integer to Number (variable)
    "aNumber = aWLong", // assign Long to Number (variable)
    "aNumber = aWFloat", // assign Float to Number (variable)
    "aNumber = aWDouble", // assign Double to Number (variable)
    "aCpaBoolean = true", // box boolean in Boolean, assign to Comparable<Boolean> (true literal)
    "aCpaBoolean = false", // box boolean in Boolean, assign to Comparable<Boolean> (false literal)
    "aCpaBoolean = aWBoolean", // assign Character to Comparable<Character> (variable)
    "aCpaChar = 'a'", // box char in Character, assign to Comparable<Character> (literal)
    "aCpaChar = aWChar", // assign Character to Comparable<Character> (variable)
    "aCpaInt = 0", // box int in Integer, assign to Comparable<Integer> (literal)
    "aCpaInt = +1", // box int in Integer, assign to Comparable<Integer> (signed literal)
    "aCpaInt = -1", // box int in Integer, assign to Comparable<Integer> (signed literal)
    "aCpaInt = aWInt", // assign Integer to Comparable<Integer> (variable)
    "aCpaLong = 0l", // box long in Long, assign to Comparable<Long> (literal)
    "aCpaLong = +1l", // box long in Long, assign to Comparable<Long> (signed literal)
    "aCpaLong = -1l", // box long in Long, assign to Comparable<Long> (signed literal)
    "aCpaLong = aWLong", // assign Long to Comparable<Long> (variable)
    "aCpaFloat = 0.0f", // box float in Float, assign to Comparable<Float> (literal)
    "aCpaFloat = +0.1f", // box float in Float, assign to Comparable<Float> (signed literal)
    "aCpaFloat = -0.1f", // box float in Float, assign to Comparable<Float> (signed literal)
    "aCpaFloat = aWFloat", // assign Float to Comparable<Float> (variable)
    "aCpaDouble = 0.0", // box double in Double, assign to Comparable<Double> (literal)
    "aCpaDouble = +0.1", // box double in Double, assign to Comparable<Double> (signed literal)
    "aCpaDouble = -0.1", // box double in Double, assign to Comparable<Double> (signed literal)
    "aCpaDouble = aWDouble", // assign Double to Comparable<Double> (variable)
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
    "aWBoolean = 'a', 0xA0179", // expected Boolean but provided char (literal)
    "aWBoolean = 0, 0xA0179", // expected Boolean but provided int (literal)
    "aWBoolean = +1, 0xA0179", // expected Boolean but provided int (signed literal)
    "aWBoolean = -1, 0xA0179", // expected Boolean but provided int (signed literal)
    "aWBoolean = 1l, 0xA0179", // Boolean but provided long (literal)
    "aWBoolean = 0.1f, 0xA0179", // Boolean but provided float (literal)
    "aWBoolean = 0.1, 0xA0179", // Boolean but provided double (literal)
    "aWBoolean = aChar, 0xA0179", // Boolean but provided char (variable)
    "aWBoolean = aShort, 0xA0179", // Boolean but provided byte (variable)
    "aWBoolean = aByte, 0xA0179", // Boolean but provided short (variable)
    "aWBoolean = anInt, 0xA0179", // Boolean but provided int (variable)
    "aWBoolean = aLong, 0xA0179", // Boolean but provided long (variable)
    "aWBoolean = aFloat, 0xA0179", // Boolean but provided float (variable)
    "aWBoolean = aDouble, 0xA0179", // Boolean but provided double (variable)
    "aWBoolean = aWChar, 0xA0179", // Boolean but provided Character (variable)
    "aWBoolean = aWShort, 0xA0179", // Boolean but provided Byte (variable)
    "aWBoolean = aWByte, 0xA0179", // Boolean but provided Short (variable)
    "aWBoolean = aWInt, 0xA0179", // Boolean but provided Integer (variable)
    "aWBoolean = aWLong, 0xA0179", // Boolean but provided Long (variable)
    "aWBoolean = aWFloat, 0xA0179", // Boolean but provided Float (variable)
    "aWBoolean = aWDouble, 0xA0179", // Boolean but provided Double (variable)
    "aWChar = true, 0xA0179", // expected Character but provided boolean (true literal)
    "aWChar = false, 0xA0179", // expected Character but provided boolean (false literal)
    "aWChar = -1, 0xA0179", // expected Character but provided int (literal, < min value)
    "aWChar = 65536, 0xA0179", // expected Character but provided int (literal, > max value)
    "aWChar = 1l, 0xA0179", // expected Character but provided long (literal)
    "aWChar = 0.1f, 0xA0179", // expected Character but provided float (literal)
    "aWChar = 0.1, 0xA0179", // expected Character but provided double (literal)
    "aWChar = aBoolean, 0xA0179", // expected Character but provided boolean (variable)
    "aWChar = aByte, 0xA0179", // expected Character but provided byte (variable)
    "aWChar = aShort, 0xA0179", // expected Character but provided short (variable)
    "aWChar = anInt, 0xA0179", // expected Character but provided int (variable)
    "aWChar = aLong, 0xA0179", // expected Character but provided long (variable)
    "aWChar = aFloat, 0xA0179", // expected Character but provided float (variable)
    "aWChar = aDouble, 0xA0179", // expected Character but provided double (variable)
    "aWChar = aWBoolean, 0xA0179", // expected Character but provided Boolean (variable)
    "aWChar = aWByte, 0xA0179", // expected Character but provided Byte (variable)
    "aWChar = aWShort, 0xA0179", // expected Character but provided Short (variable)
    "aWChar = aWInt, 0xA0179", // expected Character but provided Integer (variable)
    "aWChar = aWLong, 0xA0179", // expected Character but provided Long (variable)
    "aWChar = aWFloat, 0xA0179", // expected Character but provided Float (variable)
    "aWChar = aWDouble, 0xA0179", // expected Character but provided Double (variable)
    "aWByte = true, 0xA0179", // expected Byte but provided boolean (true literal)
    "aWByte = false, 0xA0179", // expected Byte but provided boolean (false literal)
    "aWByte = 128, 0xA0179", // expected Byte but provided int (literal, > max value)
    "aWByte = -129, 0xA0179", // expected Byte but provided int (literal, < min value)
    "aWByte = 1l, 0xA0179", // expected Byte but provided long (literal)
    "aWByte = 0.1f, 0xA0179", // expected Byte but provided float (literal)
    "aWByte = 0.1, 0xA0179", // expected Byte but provided double (literal)
    "aWByte = aBoolean, 0xA0179", // expected Byte but provided boolean (variable)
    "aWByte = aChar, 0xA0179", // expected Byte but provided char (variable)
    "aWByte = aShort, 0xA0179", // expected Byte but provided short (variable)
    "aWByte = anInt, 0xA0179", // expected Byte but provided int (variable)
    "aWByte = aLong, 0xA0179", // expected Byte but provided long (variable)
    "aWByte = aFloat, 0xA0179", // expected Byte but provided float (variable)
    "aWByte = aDouble, 0xA0179", // expected Byte but provided double (variable)
    "aWByte = aWBoolean, 0xA0179", // expected Byte but provided Boolean (variable)
    "aWByte = aWChar, 0xA0179", // expected Byte but provided Character (variable)
    "aWByte = aWShort, 0xA0179", // expected Byte but provided Short (variable)
    "aWByte = aWInt, 0xA0179", // expected Byte but provided Integer (variable)
    "aWByte = aWLong, 0xA0179", // expected Byte but provided Long (variable)
    "aWByte = aWFloat, 0xA0179", // expected Byte but provided Float (variable)
    "aWByte = aWDouble, 0xA0179", // expected Byte but provided Double (variable)
    "aWShort = true, 0xA0179", // expected Short but provided boolean (true literal)
    "aWShort = false, 0xA0179", // expected Short but provided boolean (false literal)
    "aWShort = 32768, 0xA0179", // expected Short but provided int (literal, > max value)
    "aWShort = -32769, 0xA0179", // expected Short but provided int (literal, < min value)
    "aWShort = 1l, 0xA0179", // expected Short but provided long (literal)
    "aWShort = 0.1f, 0xA0179", // expected Short but provided float (literal)
    "aWShort = 0.1, 0xA0179", // expected Short but provided double (literal)
    "aWShort = aBoolean, 0xA0179", // expected Short but provided boolean (variable)
    "aWShort = aChar, 0xA0179", // expected Short but provided char (variable)
    "aWShort = anInt, 0xA0179", // expected Short but provided int (variable)
    "aWShort = aLong, 0xA0179", // expected Short but provided long (variable)
    "aWShort = aFloat, 0xA0179", // expected Short but provided float (variable)
    "aWShort = aDouble, 0xA0179", // expected Short but provided double (variable)
    "aWShort = aWBoolean, 0xA0179", // expected Short but provided Boolean (variable)
    "aWShort = aWChar, 0xA0179", // expected Short but provided Character (variable)
    "aWShort = aWInt, 0xA0179", // expected Short but provided Integer (variable)
    "aWShort = aWLong, 0xA0179", // expected Short but provided Long (variable)
    "aWShort = aWFloat, 0xA0179", // expected Short but provided Float (variable)
    "aWShort = aWDouble, 0xA0179", // expected Short but provided Double (variable)
    "aNumber = aWBoolean, 0xA0179", // expected Number but provided Boolean (variable)
    "aNumber = aWChar, 0xA0179", // expected Number but provided Character (variable)
    "aCpaBoolean = aWChar, 0xA0179", // expected Comparable<Boolean> but provided Character (variable)
    "aCpaBoolean = aWByte, 0xA0179", // expected Comparable<Boolean> but provided Byte (variable)
    "aCpaBoolean = aWShort, 0xA0179", // expected Comparable<Boolean> but provided Short (variable)
    "aCpaBoolean = aWInt, 0xA0179", // expected Comparable<Boolean> but provided Integer (variable)
    "aCpaBoolean = aWLong, 0xA0179", // expected Comparable<Boolean> but provided Long (variable)
    "aCpaBoolean = aWFloat, 0xA0179", // expected Comparable<Boolean> but provided Float (variable)
    "aCpaBoolean = aWDouble, 0xA0179", // expected Comparable<Boolean> but provided Double (variable)
    "aCpaChar = aWBoolean, 0xA0179", // expected Comparable<Character> but provided Boolean (variable)
    "aCpaChar = aWByte, 0xA0179", // expected Comparable<Character> but provided Byte (variable)
    "aCpaChar = aWShort, 0xA0179", // expected Comparable<Character> but provided Short (variable)
    "aCpaChar = aWInt, 0xA0179", // expected Comparable<Character> but provided Integer (variable)
    "aCpaChar = aWLong, 0xA0179", // expected Comparable<Character> but provided Long (variable)
    "aCpaChar = aWFloat, 0xA0179", // expected Comparable<Character> but provided Float (variable)
    "aCpaChar = aWDouble, 0xA0179", // expected Comparable<Character> but provided Double (variable)
    "aCpaByte = aWBoolean, 0xA0179", // expected Comparable<Byte> but provided Boolean (variable)
    "aCpaByte = aWChar, 0xA0179", // expected Comparable<Byte> but provided Character (variable)
    "aCpaByte = aWShort, 0xA0179", // expected Comparable<Byte> but provided Short (variable)
    "aCpaByte = aWInt, 0xA0179", // expected Comparable<Byte> but provided Integer (variable)
    "aCpaByte = aWLong, 0xA0179", // expected Comparable<Byte> but provided Long (variable)
    "aCpaByte = aWFloat, 0xA0179", // expected Comparable<Byte> but provided Float (variable)
    "aCpaByte = aWDouble, 0xA0179", // expected Comparable<Byte> but provided Double (variable)
    "aCpaShort = aWBoolean, 0xA0179", // expected Comparable<Short> but provided Boolean (variable)
    "aCpaShort = aWChar, 0xA0179", // expected Comparable<Short> but provided Character (variable)
    "aCpaShort = aWByte, 0xA0179", // expected Comparable<Short> but provided Byte (variable)
    "aCpaShort = aWInt, 0xA0179", // expected Comparable<Short> but provided Integer (variable)
    "aCpaShort = aWLong, 0xA0179", // expected Comparable<Short> but provided Long (variable)
    "aCpaShort = aWFloat, 0xA0179", // expected Comparable<Short> but provided Float (variable)
    "aCpaShort = aWDouble, 0xA0179", // expected Comparable<Short> but provided Double (variable)
    "aCpaInt = aWBoolean, 0xA0179", // expected Comparable<Integer> but provided Boolean (variable)
    "aCpaInt = aWChar, 0xA0179", // expected Comparable<Integer> but provided Character (variable)
    "aCpaInt = aWByte, 0xA0179", // expected Comparable<Integer> but provided Byte (variable)
    "aCpaInt = aWShort, 0xA0179", // expected Comparable<Integer> but provided Short (variable)
    "aCpaInt = aWLong, 0xA0179", // expected Comparable<Integer> but provided Long (variable)
    "aCpaInt = aWFloat, 0xA0179", // expected Comparable<Integer> but provided Float (variable)
    "aCpaInt = aWDouble, 0xA0179", // expected Comparable<Integer> but provided Double (variable)
    "aCpaLong = aWBoolean, 0xA0179", // expected Comparable<Long> but provided Boolean (variable)
    "aCpaLong = aWChar, 0xA0179", // expected Comparable<Long> but provided Character (variable)
    "aCpaLong = aWByte, 0xA0179", // expected Comparable<Long> but provided Byte (variable)
    "aCpaLong = aWShort, 0xA0179", // expected Comparable<Long> but provided Short (variable)
    "aCpaLong = aWInt, 0xA0179", // expected Comparable<Long> but provided Integer (variable)
    "aCpaLong = aWFloat, 0xA0179", // expected Comparable<Long> but provided Float (variable)
    "aCpaLong = aWDouble, 0xA0179", // expected Comparable<Long> but provided Double (variable)
    "aCpaFloat = aWBoolean, 0xA0179", // expected Comparable<Float> but provided Boolean (variable)
    "aCpaFloat = aWChar, 0xA0179", // expected Comparable<Float> but provided Character (variable)
    "aCpaFloat = aWByte, 0xA0179", // expected Comparable<Float> but provided Byte (variable)
    "aCpaFloat = aWShort, 0xA0179", // expected Comparable<Float> but provided Short (variable)
    "aCpaFloat = aWInt, 0xA0179", // expected Comparable<Float> but provided Integer (variable)
    "aCpaFloat = aWLong, 0xA0179", // expected Comparable<Float> but provided Long (variable)
    "aCpaFloat = aWDouble, 0xA0179", // expected Comparable<Float> but provided Double (variable)
    "aCpaDouble = aWBoolean, 0xA0179", // expected Comparable<Double> but provided Boolean (variable)
    "aCpaDouble = aWChar, 0xA0179", // expected Comparable<Double> but provided Character (variable)
    "aCpaDouble = aWByte, 0xA0179", // expected Comparable<Double> but provided Byte (variable)
    "aCpaDouble = aWShort, 0xA0179", // expected Comparable<Double> but provided Short (variable)
    "aCpaDouble = aWInt, 0xA0179", // expected Comparable<Double> but provided Integer (variable)
    "aCpaDouble = aWLong, 0xA0179", // expected Comparable<Double> but provided Long (variable)
    "aCpaDouble = aWFloat, 0xA0179", // expected Comparable<Double> but provided Float (variable)
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
