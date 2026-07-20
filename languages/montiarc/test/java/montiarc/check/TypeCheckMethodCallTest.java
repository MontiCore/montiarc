/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._symboltable.TransitiveScopeSetter;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.CHAR;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.FLOAT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.INT;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.DOUBLE;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.SHORT;
import static org.assertj.core.api.Assertions.assertThat;


public class TypeCheckMethodCallTest extends MontiArcTestBase {
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

    // superclass
    OOTypeSymbol superClass = MontiArcMill.oOTypeSymbolBuilder()
      .setName("SuperClass")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(MontiArcMill.globalScope(), superClass);

    // method 1 of superclass
    MethodSymbol superMethod = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(superClass.getSpannedScope(), superMethod);

    // method 2 of superclass
    MethodSymbol superMethod2 = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(BOOLEAN))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(superClass.getSpannedScope(), superMethod2);

    VariableSymbol paramSup1 = MontiArcMill.variableSymbolBuilder()
      .setName("param1")
      .setType(SymTypeExpressionFactory.createPrimitive(BOOLEAN))
      .build();
    SymbolService.link(superMethod2.getSpannedScope(), paramSup1);

    VariableSymbol paramSup2 = MontiArcMill.variableSymbolBuilder()
      .setName("param2")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .build();
    SymbolService.link(superMethod2.getSpannedScope(), paramSup2);

    // ------------------------1st subclass-----------------------
    OOTypeSymbol subClass = MontiArcMill.oOTypeSymbolBuilder()
      .setName("SubClass")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(superClass))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), subClass);

    // method 1 of subclass
    MethodSymbol method1 = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(DOUBLE))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(subClass.getSpannedScope(), method1);

    VariableSymbol param1 = MontiArcMill.variableSymbolBuilder()
      .setName("param1")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .build();
    SymbolService.link(method1.getSpannedScope(), param1);

    // method 2 of subclass
    MethodSymbol method2 = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(FLOAT))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(subClass.getSpannedScope(), method2);

    VariableSymbol param2a = MontiArcMill.variableSymbolBuilder()
      .setName("param1")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .build();
    SymbolService.link(method2.getSpannedScope(), param2a);

    VariableSymbol param2b = MontiArcMill.variableSymbolBuilder()
      .setName("param2")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .build();
    SymbolService.link(method2.getSpannedScope(), param2b);

    // method3 of subclass
    MethodSymbol method3 = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(SHORT))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(subClass.getSpannedScope(), method3);

    VariableSymbol param3a = MontiArcMill.variableSymbolBuilder()
      .setName("param1")
      .setType(SymTypeExpressionFactory.createPrimitive(DOUBLE))
      .build();
    SymbolService.link(method3.getSpannedScope(), param3a);

    VariableSymbol param3b = MontiArcMill.variableSymbolBuilder()
      .setName("param2")
      .setType(SymTypeExpressionFactory.createPrimitive(DOUBLE))
      .build();
    SymbolService.link(method3.getSpannedScope(), param3b);

    // method4 of subclass
    MethodSymbol method4 = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(CHAR))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(subClass.getSpannedScope(), method4);

    VariableSymbol param4a = MontiArcMill.variableSymbolBuilder()
      .setName("param1")
      .setType(SymTypeExpressionFactory.createPrimitive(INT))
      .build();
    SymbolService.link(method4.getSpannedScope(), param4a);

    VariableSymbol param4b = MontiArcMill.variableSymbolBuilder()
      .setName("param2")
      .setType(SymTypeExpressionFactory.createPrimitive(BOOLEAN))
      .build();
    SymbolService.link(method4.getSpannedScope(), param4b);

    // ------------------------2nd subclass-----------------------
    OOTypeSymbol subClass2 = MontiArcMill.oOTypeSymbolBuilder()
      .setName("SubClass2")
      .setSpannedScope(MontiArcMill.scope())
      .addSuperTypes(SymTypeExpressionFactory.createTypeObject(superClass))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), subClass2);

    MethodSymbol method = MontiArcMill.methodSymbolBuilder()
      .setName("method")
      .setType(SymTypeExpressionFactory.createPrimitive(DOUBLE))
      .setSpannedScope(MontiArcMill.scope())
      .setIsPublic(true)
      .build();
    SymbolService.link(subClass2.getSpannedScope(), method);



// variable 1
    VariableSymbol var = MontiArcMill.variableSymbolBuilder()
      .setName("var")
      .setType(SymTypeExpressionFactory.createTypeObject(subClass))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), var);

    // variable 2
    VariableSymbol var2 = MontiArcMill.variableSymbolBuilder()
      .setName("var2")
      .setType(SymTypeExpressionFactory.createTypeObject(subClass2))
      .build();
    SymbolService.link(MontiArcMill.globalScope(), var2);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "var.method(), int", // use method of superclass
    "var.method(5), double", // use method of subclass with one integer parameter
    "'var.method(5, 10)', float", // use method of subclass with two integer parameters
    "'var.method(5.0, 10.0)', short", // use method with two double parameters
    "'var.method(5, 10.0)', short", // cast first parameter to double
    "'var.method(true, 5)', boolean", // use method of superclass
    "'var.method(5, true)', char", // use method of subclass (switched parameter order)
    "var2.method(), double", // overwrite method in superclass with same name and same amount of parameters
  })
  public void testValidExpression(@NotNull String expr, @NotNull String expectedType) throws IOException {

    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(expectedType);

    TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();
    // Given
    ASTExpression ast = MontiArcMill.parser()
      .parse_StringExpression(expr)
      .orElseThrow();
    scopeSetter.setScope(ast, this.scope);

    // When
    SymTypeExpression resultType = TypeCheck3.typeOf(ast);


    // Then
    assertThat(Log.getFindings())
      .as("Expression " + expr + " should be valid, there shouldn't be any findings.")
      .isEmpty();

    assertThat(resultType.print()).isEqualTo(expectedType);
  }

  @ParameterizedTest
  @CsvSource(value = {
    "'var.method(true, true)', 0xFD44E"
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
