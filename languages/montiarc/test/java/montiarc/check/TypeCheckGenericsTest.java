/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
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
import montiarc.util.MCError;
import montiarc._symboltable.TransitiveScopeSetter;
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
    "aGenericByte = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aGenericDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aGenericChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericBool.var = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong.var = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong.var = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble.var = aGeneric, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble.var = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericByte.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericShort.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericChar.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericInteger.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericLong.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericFloat.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aWGenericDouble.var = aWString, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aByte, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aShort, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aChar, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = anInt, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aLong, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aFloat, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aDouble, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericByte = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericShort = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericChar = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericInt = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericLong = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericFloat = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES",
    "aGenericDouble = aBool, EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES"
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
    assertThat(Log.getFindings())
      .as("Expression " + expr + " should be invalid, there should be findings.")
      .isNotEmpty();
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(error.getErrorCode());
  }


}
