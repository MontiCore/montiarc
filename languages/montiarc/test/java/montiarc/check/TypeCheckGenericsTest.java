/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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

import java.io.IOException;

public class TypeCheckGenericsTest extends MontiArcTestBase {

  protected IArcBasisScope scope;

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpSymbols();
  }

  protected void setUpSymbols() {
    this.scope = MontiArcMill.scope();
    MontiArcMill.globalScope().addSubScope(this.scope);
    this.scope.setEnclosingScope(MontiArcMill.globalScope());

    OOTypeSymbol typeA = MontiArcMill.oOTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), typeA);
    TypeVarSymbol typeVarB = MontiArcMill.typeVarSymbolBuilder()
      .setName("T")
      .build();
    IOOSymbolsScope scopeB = MontiArcMill.scope();
    SymbolService.link(scopeB, typeVarB);
    OOTypeSymbol typeB = MontiArcMill.oOTypeSymbolBuilder()
      .setName("B")
      .setSpannedScope(scopeB)
      .build();
    SymbolService.link(MontiArcMill.globalScope(), typeB);
    VariableSymbol varB = MontiArcMill.variableSymbolBuilder()
      .setName("var")
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVarB))
      .build();
    SymbolService.link(scopeB, varB);
    TypeVarSymbol typeVarC = MontiArcMill.typeVarSymbolBuilder()
      .setName("T")
      .build();
    IOOSymbolsScope scopeC = MontiArcMill.scope();
    SymbolService.link(scopeC, typeVarC);
    OOTypeSymbol typeC = MontiArcMill.oOTypeSymbolBuilder()
      .setName("C")
      .setSpannedScope(scopeC)
      .build();
    SymbolService.link(MontiArcMill.globalScope(), typeC);
    VariableSymbol varC = MontiArcMill.variableSymbolBuilder()
      .setName("var")
      .setType(SymTypeExpressionFactory.createTypeVariable(typeVarC))
      .build();
    SymbolService.link(scopeC, varC);
    FieldSymbol aGeneric = MontiArcMill.fieldSymbolBuilder()
      .setName("aGeneric")
      .setType(SymTypeExpressionFactory.createTypeObject(typeA))
      .build();
    SymbolService.link(this.scope, aGeneric);
    FieldSymbol aGenericBool = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericBool")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(BOOLEAN)))
      .build();
    SymbolService.link(this.scope, aGenericBool);
    FieldSymbol aGenericChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericChar")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(CHAR)))
      .build();
    SymbolService.link(this.scope, aGenericChar);
    FieldSymbol aGenericByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericByte")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(BYTE)))
      .build();
    SymbolService.link(this.scope, aGenericByte);
    FieldSymbol aGenericShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericShort")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(SHORT)))
      .build();
    SymbolService.link(this.scope, aGenericShort);
    FieldSymbol aGenericInt = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericInt")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(INT)))
      .build();
    SymbolService.link(this.scope, aGenericInt);
    FieldSymbol aGenericLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericLong")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(LONG)))
      .build();
    SymbolService.link(this.scope, aGenericLong);
    FieldSymbol aGenericFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericFloat")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(FLOAT)))
      .build();
    SymbolService.link(this.scope, aGenericFloat);
    FieldSymbol aGenericDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aGenericDouble")
      .setType(SymTypeExpressionFactory.createGenerics(typeB,
        SymTypeExpressionFactory.createPrimitive(DOUBLE)))
      .build();
    SymbolService.link(this.scope, aGenericDouble);
    FieldSymbol aWGenericString = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericString")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.String").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericString);
    FieldSymbol aWGenericBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericBoolean")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Boolean").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericBoolean);
    FieldSymbol aWGenericChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericChar")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Character").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericChar);
    FieldSymbol aWGenericNumber = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericNumber")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Number").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericNumber);
    FieldSymbol aWGenericByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericByte")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Byte").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericByte);
    FieldSymbol aWGenericShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericShort")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Short").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericShort);
    FieldSymbol aWGenericInteger = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericInteger")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Integer").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericInteger);
    FieldSymbol aWGenericLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericLong")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Long").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericLong);
    FieldSymbol aWGenericFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericFloat")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Float").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericFloat);
    FieldSymbol aWGenericDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aWGenericDouble")
      .setType(SymTypeExpressionFactory.createGenerics(typeC,
        SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
          .resolveOOType("java.lang.Double").orElseThrow())))
      .build();
    SymbolService.link(this.scope, aWGenericDouble);
    FieldSymbol aBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aBool")
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
    FieldSymbol aWString = MontiArcMill.fieldSymbolBuilder()
      .setName("aWString")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.String").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWString);
    FieldSymbol aWBoolean = MontiArcMill.fieldSymbolBuilder()
      .setName("aWBoolean")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Boolean").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWBoolean);
    FieldSymbol aWChar = MontiArcMill.fieldSymbolBuilder()
      .setName("aWChar")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Character").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWChar);
    FieldSymbol aNumber = MontiArcMill.fieldSymbolBuilder()
      .setName("aNumber")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Number").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aNumber);
    FieldSymbol aWByte = MontiArcMill.fieldSymbolBuilder()
      .setName("aWByte")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Byte").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWByte);
    FieldSymbol aWShort = MontiArcMill.fieldSymbolBuilder()
      .setName("aWShort")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Short").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWShort);
    FieldSymbol aWInt = MontiArcMill.fieldSymbolBuilder()
      .setName("aWInt")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Integer").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWInt);
    FieldSymbol aWLong = MontiArcMill.fieldSymbolBuilder()
      .setName("aWLong")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Long").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWLong);
    FieldSymbol aWFloat = MontiArcMill.fieldSymbolBuilder()
      .setName("aWFloat")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Float").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWFloat);
    FieldSymbol aWDouble = MontiArcMill.fieldSymbolBuilder()
      .setName("aWDouble")
      .setType(SymTypeExpressionFactory.createTypeObject(MontiArcMill.globalScope()
        .resolveOOType("java.lang.Double").orElseThrow()))
      .build();
    SymbolService.link(this.scope, aWDouble);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "aGeneric = aGeneric",
    "aGenericBool = aGenericBool",
    "aGenericChar = aGenericChar",
    "aGenericByte = aGenericByte",
    "aGenericShort = aGenericShort",
    "aGenericInt = aGenericInt",
    "aGenericLong = aGenericLong",
    "aGenericFloat = aGenericFloat",
    "aGenericDouble = aGenericDouble",
    "aWGenericString = aWGenericString",
    "aWGenericBoolean = aWGenericBoolean",
    "aWGenericChar = aWGenericChar",
    "aWGenericNumber = aWGenericNumber",
    "aWGenericByte = aWGenericByte",
    "aWGenericShort = aWGenericShort",
    "aWGenericInteger = aWGenericInteger",
    "aWGenericLong = aWGenericLong",
    "aWGenericFloat = aWGenericFloat",
    "aWGenericDouble = aWGenericDouble",
    "aGenericBool.var = aBool",
    "aGenericChar.var = aChar",
    "aGenericByte.var = aByte",
    "aGenericShort.var = aShort",
    "aGenericInt.var = anInt",
    "aGenericLong.var = aLong",
    "aGenericFloat.var = aFloat",
    "aGenericDouble.var = aDouble",
    "aWGenericString.var = aWString",
    "aWGenericBoolean.var = aBool",
    "aWGenericChar.var = aChar",
    "aWGenericByte.var = aByte",
    "aWGenericShort.var = aShort",
    "aWGenericInteger.var = anInt",
    "aWGenericLong.var = aLong",
    "aWGenericFloat.var = aFloat",
    "aWGenericDouble.var = aDouble",
    "aGenericDouble.var = aFloat",
    "aGenericDouble.var = aLong",
    "aGenericDouble.var = anInt",
    "aGenericDouble.var = aShort",
    "aGenericDouble.var = aByte",
    "aGenericFloat.var = aLong",
    "aGenericFloat.var = anInt",
    "aGenericFloat.var = aShort",
    "aGenericFloat.var = aByte",
    "aGenericLong.var = anInt",
    "aGenericLong.var = aShort",
    "aGenericLong.var = aByte",
    "aGenericInt.var = aShort",
    "aGenericInt.var = aByte",
    "aGenericShort.var = aByte",
    "aGenericInt.var = aChar",
    "aWGenericDouble.var = aFloat",
    "aWGenericDouble.var = aLong",
    "aWGenericDouble.var = anInt",
    "aWGenericDouble.var = aShort",
    "aWGenericDouble.var = aByte",
    "aWGenericFloat.var = aLong",
    "aWGenericFloat.var = anInt",
    "aWGenericFloat.var = aShort",
    "aWGenericFloat.var = aByte",
    "aWGenericLong.var = anInt",
    "aWGenericLong.var = aShort",
    "aWGenericLong.var = aByte",
    "aWGenericInteger.var = aShort",
    "aWGenericInteger.var = aByte",
    "aWGenericShort.var = aByte",
    "aWGenericInteger.var = aChar",
    "aGenericBool.var = aWBoolean",
    "aGenericChar.var = aWChar",
    "aGenericByte.var = aWByte",
    "aGenericShort.var = aWShort",
    "aGenericInt.var = aWInt",
    "aGenericLong.var = aWLong",
    "aGenericFloat.var = aWFloat",
    "aGenericDouble.var = aWDouble"
  })
  public void testValidExpression(@NotNull String expr) throws IOException {

    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();
    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    TypeCheck3.typeOf(ast);

    // Then
    assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();
  }

  @ParameterizedTest
  @CsvSource(value = {
    "aGenericByte = aGeneric, 0xA0179",
    "aGenericByte = aGenericShort, 0xA0179",
    "aGenericByte = aGenericInt, 0xA0179",
    "aGenericByte = aGenericLong, 0xA0179",
    "aGenericByte = aGenericFloat, 0xA0179",
    "aGenericByte = aGenericDouble, 0xA0179",
    "aGenericByte = aGenericChar, 0xA0179",
    "aGenericShort = aGeneric, 0xA0179",
    "aGenericShort = aGenericByte, 0xA0179",
    "aGenericShort = aGenericInt, 0xA0179",
    "aGenericShort = aGenericLong, 0xA0179",
    "aGenericShort = aGenericFloat, 0xA0179",
    "aGenericShort = aGenericDouble, 0xA0179",
    "aGenericShort = aGenericChar, 0xA0179",
    "aGenericInt = aGeneric, 0xA0179",
    "aGenericInt = aGenericChar, 0xA0179",
    "aGenericInt = aGenericByte, 0xA0179",
    "aGenericInt = aGenericShort, 0xA0179",
    "aGenericInt = aGenericLong, 0xA0179",
    "aGenericInt = aGenericFloat, 0xA0179",
    "aGenericInt = aGenericDouble, 0xA0179",
    "aGenericLong = aGeneric, 0xA0179",
    "aGenericLong = aGenericShort, 0xA0179",
    "aGenericLong = aGenericInt, 0xA0179",
    "aGenericLong = aGenericByte, 0xA0179",
    "aGenericLong = aGenericFloat, 0xA0179",
    "aGenericLong = aGenericDouble, 0xA0179",
    "aGenericLong = aGenericChar, 0xA0179",
    "aGenericFloat = aGeneric, 0xA0179",
    "aGenericFloat = aGenericShort, 0xA0179",
    "aGenericFloat = aGenericInt, 0xA0179",
    "aGenericFloat = aGenericLong, 0xA0179",
    "aGenericFloat = aGenericByte, 0xA0179",
    "aGenericFloat = aGenericDouble, 0xA0179",
    "aGenericFloat = aGenericChar, 0xA0179",
    "aGenericDouble = aGeneric, 0xA0179",
    "aGenericDouble = aGenericShort, 0xA0179",
    "aGenericDouble = aGenericInt, 0xA0179",
    "aGenericDouble = aGenericLong, 0xA0179",
    "aGenericDouble = aGenericFloat, 0xA0179",
    "aGenericDouble = aGenericByte, 0xA0179",
    "aGenericDouble = aGenericChar, 0xA0179",
    "aGenericBool.var = aGeneric, 0xA0179",
    "aGenericBool.var = aShort, 0xA0179",
    "aGenericBool.var = anInt, 0xA0179",
    "aGenericBool.var = aLong, 0xA0179",
    "aGenericBool.var = aFloat, 0xA0179",
    "aGenericBool.var = aDouble, 0xA0179",
    "aGenericBool.var = aChar, 0xA0179",
    "aGenericByte.var = aGeneric, 0xA0179",
    "aGenericByte.var = aBool, 0xA0179",
    "aGenericByte.var = aShort, 0xA0179",
    "aGenericByte.var = anInt, 0xA0179",
    "aGenericByte.var = aLong, 0xA0179",
    "aGenericByte.var = aFloat, 0xA0179",
    "aGenericByte.var = aDouble, 0xA0179",
    "aGenericByte.var = aChar, 0xA0179",
    "aGenericShort.var = aGeneric, 0xA0179",
    "aGenericShort.var = aBool, 0xA0179",
    "aGenericShort.var = anInt, 0xA0179",
    "aGenericShort.var = aLong, 0xA0179",
    "aGenericShort.var = aFloat, 0xA0179",
    "aGenericShort.var = aDouble, 0xA0179",
    "aGenericShort.var = aChar, 0xA0179",
    "aGenericChar.var = aGeneric, 0xA0179",
    "aGenericChar.var = aBool, 0xA0179",
    "aGenericChar.var = anInt, 0xA0179",
    "aGenericChar.var = aLong, 0xA0179",
    "aGenericChar.var = aFloat, 0xA0179",
    "aGenericChar.var = aDouble, 0xA0179",
    "aGenericInt.var = aGeneric, 0xA0179",
    "aGenericInt.var = aBool, 0xA0179",
    "aGenericInt.var = aLong, 0xA0179",
    "aGenericInt.var = aFloat, 0xA0179",
    "aGenericInt.var = aDouble, 0xA0179",
    "aGenericLong.var = aGeneric, 0xA0179",
    "aGenericLong.var = aBool, 0xA0179",
    "aGenericLong.var = aFloat, 0xA0179",
    "aGenericLong.var = aDouble, 0xA0179",
    "aGenericFloat.var = aGeneric, 0xA0179",
    "aGenericFloat.var = aBool, 0xA0179",
    "aGenericDouble.var = aGeneric, 0xA0179",
    "aGenericDouble.var = aBool, 0xA0179",
    "aGenericByte.var = aWString, 0xA0179",
    "aGenericShort.var = aWString, 0xA0179",
    "aGenericChar.var = aWString, 0xA0179",
    "aGenericInt.var = aWString, 0xA0179",
    "aGenericLong.var = aWString, 0xA0179",
    "aGenericFloat.var = aWString, 0xA0179",
    "aGenericDouble.var = aWString, 0xA0179",
    "aWGenericByte.var = aWString, 0xA0179",
    "aWGenericShort.var = aWString, 0xA0179",
    "aWGenericChar.var = aWString, 0xA0179",
    "aWGenericInteger.var = aWString, 0xA0179",
    "aWGenericLong.var = aWString, 0xA0179",
    "aWGenericFloat.var = aWString, 0xA0179",
    "aWGenericDouble.var = aWString, 0xA0179",
    "aGenericByte = aByte, 0xA0179",
    "aGenericShort = aByte, 0xA0179",
    "aGenericChar = aByte, 0xA0179",
    "aGenericInt = aByte, 0xA0179",
    "aGenericLong = aByte, 0xA0179",
    "aGenericFloat = aByte, 0xA0179",
    "aGenericDouble = aByte, 0xA0179",
    "aGenericByte = aShort, 0xA0179",
    "aGenericShort = aShort, 0xA0179",
    "aGenericChar = aShort, 0xA0179",
    "aGenericInt = aShort, 0xA0179",
    "aGenericLong = aShort, 0xA0179",
    "aGenericFloat = aShort, 0xA0179",
    "aGenericDouble = aShort, 0xA0179",
    "aGenericByte = aChar, 0xA0179",
    "aGenericShort = aChar, 0xA0179",
    "aGenericChar = aChar, 0xA0179",
    "aGenericInt = aChar, 0xA0179",
    "aGenericLong = aChar, 0xA0179",
    "aGenericFloat = aChar, 0xA0179",
    "aGenericDouble = aChar, 0xA0179",
    "aGenericByte = anInt, 0xA0179",
    "aGenericShort = anInt, 0xA0179",
    "aGenericChar = anInt, 0xA0179",
    "aGenericInt = anInt, 0xA0179",
    "aGenericLong = anInt, 0xA0179",
    "aGenericFloat = anInt, 0xA0179",
    "aGenericDouble = anInt, 0xA0179",
    "aGenericByte = aLong, 0xA0179",
    "aGenericShort = aLong, 0xA0179",
    "aGenericChar = aLong, 0xA0179",
    "aGenericInt = aLong, 0xA0179",
    "aGenericLong = aLong, 0xA0179",
    "aGenericFloat = aLong, 0xA0179",
    "aGenericDouble = aLong, 0xA0179",
    "aGenericByte = aFloat, 0xA0179",
    "aGenericShort = aFloat, 0xA0179",
    "aGenericChar = aFloat, 0xA0179",
    "aGenericInt = aFloat, 0xA0179",
    "aGenericLong = aFloat, 0xA0179",
    "aGenericFloat = aFloat, 0xA0179",
    "aGenericDouble = aFloat, 0xA0179",
    "aGenericByte = aDouble, 0xA0179",
    "aGenericShort = aDouble, 0xA0179",
    "aGenericChar = aDouble, 0xA0179",
    "aGenericInt = aDouble, 0xA0179",
    "aGenericLong = aDouble, 0xA0179",
    "aGenericFloat = aDouble, 0xA0179",
    "aGenericDouble = aDouble, 0xA0179",
    "aGenericByte = aBool, 0xA0179",
    "aGenericShort = aBool, 0xA0179",
    "aGenericChar = aBool, 0xA0179",
    "aGenericInt = aBool, 0xA0179",
    "aGenericLong = aBool, 0xA0179",
    "aGenericFloat = aBool, 0xA0179",
    "aGenericDouble = aBool, 0xA0179"
  })
  public void testInvalidExpression(@NotNull String expr, @NotNull String error) throws IOException {

    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(error);
    Preconditions.checkArgument(!expr.isBlank());
    Preconditions.checkArgument(!error.isBlank());

    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();
    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    TypeCheck3.typeOf(ast);

    // Then
    assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(error);
  }


}
