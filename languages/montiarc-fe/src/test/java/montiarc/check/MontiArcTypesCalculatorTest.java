/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypesCalculatorTest;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types.check.LastResult;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.typesymbols._symboltable.*;
import montiarc.MontiArcMill;
import montiarc.TypeSymbolsMillForMontiArc;
import montiarc._parser.MontiArcParser;
import montiarc._visitor.IMontiArcDelegatorVisitor;
import montiarc._visitor.MontiArcDelegatorVisitor;
import montiarc._visitor.MontiArcInheritanceVisitor;
import montiarc.util.check.IArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class MontiArcTypesCalculatorTest extends AbstractArcTypesCalculatorTest {

  protected MontiArcParser parser;

  protected static Stream<Arguments> expressionProviderForGenericFields() {
    return Stream.of(Arguments.of("strBuffer", "Buffer<String>"), Arguments.of("roleBuffer", "Buffer<Role>"),
      Arguments.of("msgStorage", "Storage<Message>"), Arguments.of("ma2java", "Trafo<Student,Teacher>"));
  }

  protected static Stream<Arguments> expressionProviderWithMethodCalls() {
    return Stream.of(Arguments.of("msg.getHeader()", "String"), Arguments.of("msg.setHeader(\"0x\")", "String"));
  }

  protected static Stream<Arguments> expressionProviderWithGenericMethodCalls() {
    return Stream.of(Arguments.of("trafoBuilder.build()", "Trafo<Student,Teacher>"));
  }

  protected static Stream<Arguments> expressionProviderWithAssignments() {
    return Stream.of(Arguments.of("5 + 4", "int"), Arguments.of("a + 6", "int"), Arguments.of("a + b", "int"));
  }

  @BeforeEach
  public void setUpGenericTypes() {
    TypeVarSymbol bT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    TypeSymbol buffer = MontiArcMill.typeSymbolBuilder().setName("Buffer")
      .setSpannedScope(TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build())
      .setTypeParameterList(Collections.singletonList(bT)).build();
    TypeVarSymbol sT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    TypeSymbol storage = MontiArcMill.typeSymbolBuilder().setName("Storage")
      .setSpannedScope(TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build())
      .setTypeParameterList(Collections.singletonList(sT)).build();
    TypeVarSymbol tT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    TypeVarSymbol tV = MontiArcMill.typeVarSymbolBuilder().setName("V").build();
    TypeSymbol trafo = MontiArcMill.typeSymbolBuilder().setName("Trafo")
      .setSpannedScope(TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build())
      .setTypeParameterList(Arrays.asList(tT, tV)).build();
    this.add2Scope(this.getScope(), buffer, storage, trafo);
  }

  @BeforeEach
  public void setUpMessageObject() {
    TypeSymbolsScope getHeaderScope = TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build();
    MethodSymbol getHeader = MontiArcMill.methodSymbolBuilder().setName("getHeader")
      .setReturnType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setIsStatic(false).setSpannedScope(getHeaderScope).build();
    getHeaderScope.setSpanningSymbol(getHeader);
    getHeader.setSpannedScope(getHeaderScope);
    TypeSymbolsScope setHeaderScope = TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build();
    FieldSymbol setHeaderPara = ArcBasisMill.fieldSymbolBuilder().setName("content")
      .setType(SymTypeExpressionFactory.createTypeExpression("String", setHeaderScope))
      .setIsParameter(true).build();
    setHeaderScope.add(setHeaderPara);
    MethodSymbol setHeader = MontiArcMill.methodSymbolBuilder().setName("setHeader")
      .setReturnType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setIsStatic(false).setSpannedScope(setHeaderScope).build();
    setHeaderScope.setSpanningSymbol(setHeader);
    setHeader.setSpannedScope(setHeaderScope);
    TypeSymbolsScope msgScope = TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder()
      .addSubScope(getHeaderScope).addSubScope(setHeaderScope).build();
    TypeSymbol msg = MontiArcMill.typeSymbolBuilder().setName("Message")
      .setSpannedScope(msgScope).setMethodList(Arrays.asList(getHeader, setHeader)).build();
    this.add2Scope(this.getScope(), msg);
  }

  @BeforeEach
  public void setUpTrafoBuilder() {
    TypeSymbolsScope buildScope = TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder().build();
    List<SymTypeExpression> trafoArgs = Arrays.asList(
      SymTypeExpressionFactory.createTypeVariable("Student", this.getScope()),
      SymTypeExpressionFactory.createTypeVariable("Teacher", this.getScope()));
    MethodSymbol build = MontiArcMill.methodSymbolBuilder().setName("build")
      .setReturnType(SymTypeExpressionFactory.createGenerics("Trafo", this.getScope(), trafoArgs))
      .setIsStatic(false).setSpannedScope(buildScope).build();
    buildScope.setSpanningSymbol(build);
    build.setSpannedScope(buildScope);
    TypeSymbolsScope builderScope = TypeSymbolsMillForMontiArc.typeSymbolsScopeBuilder()
      .addSubScope(buildScope).build();
    TypeSymbol builder = MontiArcMill.typeSymbolBuilder().setName("TrafoBuilder")
      .setSpannedScope(builderScope).setMethodList(Collections.singletonList(build)).build();
    this.add2Scope(this.getScope(), builder);
  }

  @BeforeEach
  public void setUpGenericFields() {
    SymTypeExpression bufferArg = SymTypeExpressionFactory.createTypeVariable("String", this.getScope());
    FieldSymbol buffer = ArcBasisMill.fieldSymbolBuilder().setName("strBuffer")
      .setType(SymTypeExpressionFactory.createGenerics("Buffer", this.getScope(), bufferArg)).build();
    SymTypeExpression buffer2Arg = SymTypeExpressionFactory.createTypeVariable("Role", this.getScope());
    FieldSymbol buffer2 = ArcBasisMill.fieldSymbolBuilder().setName("roleBuffer")
      .setType(SymTypeExpressionFactory.createGenerics("Buffer", this.getScope(), buffer2Arg)).build();
    SymTypeExpression storageArg = SymTypeExpressionFactory.createTypeVariable("Message", this.getScope());
    FieldSymbol storage = ArcBasisMill.fieldSymbolBuilder().setName("msgStorage")
      .setType(SymTypeExpressionFactory.createGenerics("Storage", this.getScope(), storageArg)).build();
    List<SymTypeExpression> ma2javaArgs = Arrays.asList(
      SymTypeExpressionFactory.createTypeVariable("Student", this.getScope()),
      SymTypeExpressionFactory.createTypeVariable("Teacher", this.getScope()));
    FieldSymbol trafo = ArcBasisMill.fieldSymbolBuilder().setName("ma2java")
      .setType(SymTypeExpressionFactory.createGenerics("Trafo", this.getScope(), ma2javaArgs)).build();
    this.add2Scope(this.getScope(), buffer, buffer2, storage, trafo);
  }

  @BeforeEach
  public void setUpMsgFields() {
    FieldSymbol msg = ArcBasisMill.fieldSymbolBuilder().setName("msg")
      .setType(SymTypeExpressionFactory.createTypeExpression("Message", this.getScope())).build();
    this.add2Scope(this.getScope(), msg);
  }

  @BeforeEach
  public void setUpTrafoBuilderFields() {
    FieldSymbol builder = ArcBasisMill.fieldSymbolBuilder().setName("trafoBuilder")
      .setType(SymTypeExpressionFactory.createTypeExpression("TrafoBuilder", this.getScope())).build();
    this.add2Scope(this.getScope(), builder);
  }

  /**
   * Method under test {@link MontiArcTypesCalculator#calculateType(ASTExpression)}
   */
  @ParameterizedTest
  @MethodSource("expressionProviderForGenericFields")
  public void shouldCalculateGenericType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromNameExpression(expression, expectedType, false, true);
  }

  /**
   * Method under test {@link MontiArcTypesCalculator#calculateType(ASTExpression)}
   */
  @ParameterizedTest
  @MethodSource("expressionProviderWithMethodCalls")
  public void shouldCalculateObjectTypeFromMethodCalls(@NotNull String expression, @NotNull String expectedType) throws IOException {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromExpression(expression, expectedType, false, false);
  }

  /**
   * Method under test {@link MontiArcTypesCalculator#calculateType(ASTExpression)}
   */
  @ParameterizedTest
  @MethodSource("expressionProviderWithGenericMethodCalls")
  public void shouldCalculateObjectTypeFromGenericMethodCalls(@NotNull String expression, @NotNull String expectedType) throws IOException {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromExpression(expression, expectedType, false, true);
  }

  /**
   * Method under test {@link MontiArcTypesCalculator#calculateType(ASTExpression)}
   */
  @ParameterizedTest
  @MethodSource("expressionProviderWithAssignments")
  public void shouldCalculatePrimitiveTypeFromAssignment(@NotNull String expression, @NotNull String expectedType) throws IOException {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doCalculateTypeFromExpression(expression, expectedType, true, false);
  }

  protected void doCalculateTypeFromExpression(@NotNull String expression, @NotNull String expectedType,
                                               boolean isPrimitive, boolean isGeneric) throws IOException {
    Preconditions.checkArgument(expression != null);
    Preconditions.checkArgument(expectedType != null);
    doShouldCalculateType(doParseExpression(expression), expectedType, isPrimitive, isGeneric);
  }

  protected ASTExpression doParseExpression(@NotNull String expression) throws IOException {
    Preconditions.checkArgument(expression != null);
    return this.getParser().parse_StringExpression(expression).orElse(null);
  }

  @Override
  protected IArcTypesCalculator getTypesCalculator() {
    if (this.typesCalculator == null) {
      this.typesCalculator = new MontiArcTypesCalculator(new LastResult());
    }
    return this.typesCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = MontiArcMill.montiArcScopeBuilder().build();
    }
    return this.scope;
  }

  @Override
  protected ArcBasisExpressionsScopeSetter getScopeSetter() {
    if (this.scopeSetter == null) {
      this.scopeSetter = new MontiArcExpressionsScopeSetter(this.getScope());
    }
    return this.scopeSetter;
  }

  protected MontiArcParser getParser() {
    if (this.parser == null) {
      this.parser = new MontiArcParser();
    }
    return parser;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    MontiArcTypesCalculator typesCalculator = new MontiArcTypesCalculator(new LastResult());

    //When
    IMontiArcDelegatorVisitor delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator instanceof MontiArcDelegatorVisitor);
  }


  public static class MontiArcExpressionsScopeSetter extends ArcBasisExpressionsScopeSetter implements MontiArcInheritanceVisitor {

    @Override
    public MontiArcExpressionsScopeSetter getRealThis() {
      return this;
    }

    public MontiArcExpressionsScopeSetter(@NotNull IExpressionsBasisScope scope) {
      super(scope);
    }
  }
}