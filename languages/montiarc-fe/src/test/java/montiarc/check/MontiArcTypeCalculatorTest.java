/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import montiarc.MontiArcMill;
import montiarc._auxiliary.OOSymbolsMillForMontiArc;
import montiarc._parser.MontiArcParser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds test for methods of {@link MontiArcTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class MontiArcTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

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

  @Override
  @BeforeEach
  public void init() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected void setUp() {
    super.setUp();
    this.setUpMessageType();
    this.setUpMsgFields();
    this.setUpGenericTypes();
    this.setUpGenericFields();
    this.setUpTrafoBuilderType();
    this.setUpTrafoBuilderFields();
  }

  public void setUpGenericTypes() {
    TypeVarSymbol bT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    IOOSymbolsScope bS = MontiArcMill.scope();
    bS.add(bT);
    OOTypeSymbol buffer = MontiArcMill.oOTypeSymbolBuilder().setName("Buffer").setSpannedScope(bS).build();
    TypeVarSymbol sT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    IOOSymbolsScope sS = MontiArcMill.scope();
    sS.add(sT);
    OOTypeSymbol storage = MontiArcMill.oOTypeSymbolBuilder().setName("Storage").setSpannedScope(sS).build();
    TypeVarSymbol tT = MontiArcMill.typeVarSymbolBuilder().setName("T").build();
    TypeVarSymbol tV = MontiArcMill.typeVarSymbolBuilder().setName("V").build();
    IOOSymbolsScope tS = MontiArcMill.scope();
    tS.add(tT);
    tS.add(tV);
    OOTypeSymbol trafo = MontiArcMill.oOTypeSymbolBuilder().setName("Trafo").setSpannedScope(tS).build();
    SymbolService.link(this.getScope(), buffer, storage, trafo);
  }

  public void setUpMessageType() {
    IOOSymbolsScope getHeaderScope = MontiArcMill.scope();
    FunctionSymbol getHeader = MontiArcMill.functionSymbolBuilder().setName("getHeader")
      .setReturnType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setSpannedScope(getHeaderScope).build();
    getHeaderScope.setSpanningSymbol(getHeader);
    getHeader.setSpannedScope(getHeaderScope);
    IOOSymbolsScope setHeaderScope = MontiArcMill.scope();
    VariableSymbol setHeaderPara = ArcBasisMill.variableSymbolBuilder().setName("content")
      .setType(SymTypeExpressionFactory.createTypeExpression("String", setHeaderScope)).build();
    setHeaderScope.add(setHeaderPara);
    FunctionSymbol setHeader = MontiArcMill.functionSymbolBuilder().setName("setHeader")
      .setReturnType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setSpannedScope(setHeaderScope).build();
    setHeaderScope.setSpanningSymbol(setHeader);
    setHeader.setSpannedScope(setHeaderScope);
    IOOSymbolsScope msgScope = MontiArcMill.scope();
    msgScope.addSubScope(getHeaderScope);
    msgScope.addSubScope(setHeaderScope);
    msgScope.add(getHeader);
    msgScope.add(setHeader);
    OOTypeSymbol msg = MontiArcMill.oOTypeSymbolBuilder().setName("Message").setSpannedScope(msgScope).build();
    SymbolService.link(this.getScope(), msg);
  }

  public void setUpTrafoBuilderType() {
    IOOSymbolsScope buildScope = OOSymbolsMillForMontiArc.scope();
    List<SymTypeExpression> trafoArgs = Arrays.asList(
      SymTypeExpressionFactory.createTypeVariable("Student", this.getScope()),
      SymTypeExpressionFactory.createTypeVariable("Teacher", this.getScope()));
    FunctionSymbol build = MontiArcMill.functionSymbolBuilder().setName("build")
      .setReturnType(SymTypeExpressionFactory.createGenerics("Trafo", this.getScope(), trafoArgs))
      .setSpannedScope(buildScope).build();
    buildScope.setSpanningSymbol(build);
    build.setSpannedScope(buildScope);
    IOOSymbolsScope builderScope = OOSymbolsMillForMontiArc.scope();
    builderScope.addSubScope(buildScope);
    builderScope.add(build);
    OOTypeSymbol builder =
      MontiArcMill.oOTypeSymbolBuilder().setName("TrafoBuilder").setSpannedScope(builderScope).build();
    SymbolService.link(this.getScope(), builder);
  }

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
    SymbolService.link(this.getScope(), buffer, buffer2, storage, trafo);
  }

  public void setUpMsgFields() {
    FieldSymbol msg = ArcBasisMill.fieldSymbolBuilder().setName("msg")
      .setType(SymTypeExpressionFactory.createTypeExpression("Message", this.getScope())).build();
    SymbolService.link(this.getScope(), msg);
  }

  public void setUpTrafoBuilderFields() {
    FieldSymbol builder = ArcBasisMill.fieldSymbolBuilder().setName("trafoBuilder")
      .setType(SymTypeExpressionFactory.createTypeExpression("TrafoBuilder", this.getScope())).build();
    SymbolService.link(this.getScope(), builder);
  }

  @ParameterizedTest
  @MethodSource("expressionProviderForGenericFields")
  public void shouldCalculateGenericType(@NotNull String expression, @NotNull String expectedType) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromNameExpression(expression, expectedType, false, true);
  }

  @ParameterizedTest
  @MethodSource("expressionProviderWithMethodCalls")
  public void shouldCalculateObjectTypeFromMethodCalls(@NotNull String expression, @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromExpression(expression, expectedType, false, false);
  }

  @ParameterizedTest
  @MethodSource("expressionProviderWithGenericMethodCalls")
  public void shouldCalculateObjectTypeFromGenericMethodCalls(@NotNull String expression,
                                                              @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromExpression(expression, expectedType, false, true);
  }

  @ParameterizedTest
  @MethodSource("expressionProviderWithAssignments")
  public void shouldCalculatePrimitiveTypeFromAssignment(@NotNull String expression, @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doCalculateTypeFromExpression(expression, expectedType, true, false);
  }

  protected void doCalculateTypeFromExpression(@NotNull String expression, @NotNull String expectedType,
                                               boolean isPrimitive, boolean isGeneric) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);
    doShouldCalculateType(doParseExpression(expression), expectedType, isPrimitive, isGeneric);
  }

  protected ASTExpression doParseExpression(@NotNull String expression) throws IOException {
    Preconditions.checkNotNull(expression);
    return this.getParser().parse_StringExpression(expression).orElse(null);
  }

  @Override
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new MontiArcTypeCalculator(new TypeCheckResult());
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = MontiArcMill.scope();
    }
    return this.scope;
  }

  protected MontiArcParser getParser() {
    if (this.parser == null) {
      this.parser = MontiArcMill.parser();
    }
    return parser;
  }
}