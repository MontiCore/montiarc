/* (c) https://github.com/MontiCore/monticore */
package montiarc.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheckResult;
import montiarc.MontiArcMill;
import montiarc._auxiliary.OOSymbolsMillForMontiArc;
import montiarc._parser.MontiArcParser;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MontiArcTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

  protected MontiArcParser parser;

  protected static Stream<Arguments> expressionProviderForGenericFields() {
    return Stream.of(
      Arguments.of("strBuffer", "Buffer<String>"),
      Arguments.of("roleBuffer", "Buffer<Role>"),
      Arguments.of("msgStorage", "Storage<Message>"),
      Arguments.of("ma2java", "Trafo<Student,Teacher>")
    );
  }

  protected static Stream<Arguments> expressionProviderWithMethodCalls() {
    return Stream.of(
      Arguments.of("msg.getHeader()", "String"),
      Arguments.of("msg.setHeader(\"0x\")", "String")
      // Arguments.of("Message.Message()", "Message")
    );
  }

  protected static Stream<Arguments> expressionProviderWithGenericMethodCalls() {
    return Stream.of(Arguments.of("trafoBuilder.build()", "Trafo<Student,Teacher>"));
  }

  protected static Stream<Arguments> expressionProviderWithAssignments() {
    return Stream.of(Arguments.of("5 + 4", "int"), Arguments.of("a + 6", "int"), Arguments.of("a + b", "int"));
  }

  protected static Stream<Arguments> expressionWithOneIllegalSubExpression() {
    return Stream.of(
      // Assignment expressions
      Arguments.of("invalid++"),
      Arguments.of("invalid--"),
      Arguments.of("++invalid"),
      Arguments.of("--invalid"),
      Arguments.of("invalid = intField"),
      Arguments.of("intField = invalid"),
      Arguments.of("invalid += intField"),
      Arguments.of("intField += invalid"),
      Arguments.of("invalid -= intField"),
      Arguments.of("intField -= invalid"),
      Arguments.of("invalid *= intField"),
      Arguments.of("intField *= invalid"),
      Arguments.of("invalid /= intField"),
      Arguments.of("intField /= invalid"),
      Arguments.of("invalid &= intField"),
      Arguments.of("intField &= invalid"),
      Arguments.of("invalid |= intField"),
      Arguments.of("intField |= invalid"),
      Arguments.of("invalid ^= intField"),
      Arguments.of("intField ^= invalid"),
      Arguments.of("invalid >>= intField"),
      Arguments.of("intField >>= invalid"),
      Arguments.of("invalid >>>= intField"),
      Arguments.of("intField >>>= invalid"),
      Arguments.of("invalid <<= intField"),
      Arguments.of("intField <<= invalid"),
      Arguments.of("invalid %= intField"),
      Arguments.of("intField %= invalid"),
      Arguments.of("invalid += \"\""),
      Arguments.of("\"\" += invalid"),

      // Common expressions
      Arguments.of("invalid().foo"),
      //Arguments.of("invalid.foo"),
      //Arguments.of("(\"\" / 2).foo()"),
      Arguments.of("invalid.foo()"),
      Arguments.of("TypeWithMethods.staticMethod(1, invalid2)"),
      Arguments.of("TypeWithMethods.staticMethod(invalid1, 2)"),
      Arguments.of("TypeWithMethods.staticMethodVarargs(1, invalid2)"),
      Arguments.of("TypeWithMethods.staticMethodVarargs(invalid1, 2)"),
      Arguments.of("instanceWithMethods.instanceMethod(1, invalid2)"),
      Arguments.of("instanceWithMethods.instanceMethod(invalid1, 2)"),
      Arguments.of("instanceWithMethods.instanceMethodVarargs(1, invalid2)"),
      Arguments.of("instanceWithMethods.instanceMethodVarargs(invalid1, 2)"),
      Arguments.of("+invalid"),
      Arguments.of("-invalid"),
      Arguments.of("~invalid"),
      Arguments.of("!invalid"),
      Arguments.of("invalid * 12"),
      Arguments.of("12 * invalid"),
      Arguments.of("invalid / 12"),
      Arguments.of("12 / invalid"),
      Arguments.of("invalid % 12"),
      Arguments.of("12 % invalid"),
      Arguments.of("invalid + 12"),
      Arguments.of("12 + invalid"),
      Arguments.of("invalid - 12"),
      Arguments.of("12 - invalid"),
      Arguments.of("invalid <= 12"),
      Arguments.of("12 <= invalid"),
      Arguments.of("invalid >= 12"),
      Arguments.of("12 >= invalid"),
      Arguments.of("invalid < 12"),
      Arguments.of("12 < invalid"),
      Arguments.of("invalid > 12"),
      Arguments.of("12 > invalid"),
      Arguments.of("invalid == 12"),
      Arguments.of("12 == invalid"),
      Arguments.of("invalid != 12"),
      Arguments.of("12 != invalid"),
      Arguments.of("invalid && 12"),
      Arguments.of("12 && invalid"),
      Arguments.of("invalid || 12"),
      Arguments.of("12 || invalid"),
      Arguments.of("invalid + \"\""),
      Arguments.of("\"\" + invalid"),
      Arguments.of("true ? invalid2 : 3"),
      //Arguments.of("true ? 2 : invalid3"),
      Arguments.of("(invalid)")
    );
  }

  protected static Stream<Arguments> expressionWithIllegalSubExpressions() {
    return Stream.of(
      // Assignment expressions
      Arguments.of("invalid1 = invalid2"),
      Arguments.of("invalid1 += invalid2"),
      Arguments.of("invalid1 -= invalid2"),
      Arguments.of("invalid1 *= invalid2"),
      Arguments.of("invalid1 /= invalid2"),
      Arguments.of("invalid1 &= invalid2"),
      Arguments.of("invalid1 |= invalid2"),
      Arguments.of("invalid1 ^= invalid2"),
      Arguments.of("invalid1 >>= invalid2"),
      Arguments.of("invalid1 >>>= invalid2"),
      Arguments.of("invalid1 <<= invalid2"),
      Arguments.of("invalid1 %= invalid2"),

      // Common expressions
      //Arguments.of("(\"\" / 2).invalidM(invalid1, invalid2)"),
      Arguments.of("invalid1 * invalid2"),
      Arguments.of("invalid1 / invalid2"),
      Arguments.of("invalid1 % invalid2"),
      Arguments.of("invalid1 + invalid2"),
      Arguments.of("invalid1 - invalid2"),
      Arguments.of("invalid1 <= invalid2"),
      Arguments.of("invalid1 >= invalid2"),
      Arguments.of("invalid1 < invalid2"),
      Arguments.of("invalid1 > invalid2"),
      Arguments.of("invalid1 == invalid2"),
      Arguments.of("invalid1 != invalid2"),
      Arguments.of("invalid1 && invalid2"),
      Arguments.of("invalid1 || invalid2"),
      Arguments.of("invalid ? invalid2 : invalid3")
    );
  }

  protected static Stream<Arguments> expressionOnlyWithIllegalSubExpressionsAndQualifiedNameProvider() {
    return Stream.of(
      Arguments.of("invalidM(invalid1, invalid2)", new String[]{"invalidM"}),
      Arguments.of("invalidCallee.invalidM(invalid1, invalid2)", new String[]{"invalidCallee"})
    );
  }

  @Override
  @BeforeEach
  public void setUp() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.reset();
    MontiArcMill.init();
    addBasicTypes2Scope();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    this.setUpScope();
  }

  @Override
  protected void setUpScope() {
    super.setUpScope();
    this.setUpMessageType();
    this.setUpMsgFields();
    this.setUpGenericTypes();
    this.setUpGenericFields();
    this.setUpTrafoBuilderType();
    this.setUpTrafoBuilderFields();
    this.setUpPrimitiveFields();
    this.setUpTypeAndInstanceWithMethods();
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
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .setType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setSpannedScope(getHeaderScope).build();
    getHeaderScope.setSpanningSymbol(getHeader);
    getHeader.setSpannedScope(getHeaderScope);
    IOOSymbolsScope setHeaderScope = MontiArcMill.scope();
    VariableSymbol setHeaderPara = ArcBasisMill.variableSymbolBuilder().setName("content")
      .setType(SymTypeExpressionFactory.createTypeExpression("String", setHeaderScope)).build();
    setHeaderScope.add(setHeaderPara);
    FunctionSymbol setHeader = MontiArcMill.functionSymbolBuilder().setName("setHeader")
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .setType(SymTypeExpressionFactory.createTypeExpression("String", this.getScope()))
      .setSpannedScope(setHeaderScope).build();
    setHeaderScope.setSpanningSymbol(setHeader);
    setHeader.setSpannedScope(setHeaderScope);
    IOOSymbolsScope constructorScope = MontiArcMill.scope();
    MethodSymbol constructor = MontiArcMill.methodSymbolBuilder().setName("Message")
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .setType(SymTypeExpressionFactory.createTypeExpression("Message", this.getScope()))
      .setSpannedScope(constructorScope).setIsConstructor(true).setIsStatic(false).build();
    constructorScope.setSpanningSymbol(constructor);
    constructor.setSpannedScope(constructorScope);
    IOOSymbolsScope msgScope = MontiArcMill.scope();
    msgScope.addSubScope(getHeaderScope);
    msgScope.addSubScope(setHeaderScope);
    msgScope.addSubScope(constructorScope);
    msgScope.add(getHeader);
    msgScope.add(setHeader);
    msgScope.add(constructor);
    OOTypeSymbol msg = MontiArcMill.oOTypeSymbolBuilder().setName("Message").setSpannedScope(msgScope).build();
    SymbolService.link(this.getScope(), msg);
  }

  public void setUpTrafoBuilderType() {
    IOOSymbolsScope buildScope = OOSymbolsMillForMontiArc.scope();
    List<SymTypeExpression> trafoArgs = Arrays.asList(
      SymTypeExpressionFactory.createTypeVariable("Student", this.getScope()),
      SymTypeExpressionFactory.createTypeVariable("Teacher", this.getScope()));
    FunctionSymbol build = MontiArcMill.functionSymbolBuilder().setName("build")
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .setType(SymTypeExpressionFactory.createGenerics("Trafo", this.getScope(), trafoArgs))
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

  public void setUpPrimitiveFields() {
    FieldSymbol builder = ArcBasisMill.fieldSymbolBuilder().setName("intField")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    SymbolService.link(this.getScope(), builder);
    FieldSymbol builder2 = ArcBasisMill.fieldSymbolBuilder().setName("boolField")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    SymbolService.link(this.getScope(), builder2);
  }

  public void setUpTypeAndInstanceWithMethods() {
    OOTypeSymbol type = MontiArcMill.oOTypeSymbolBuilder()
      .setName("TypeWithMethods")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    SymbolService.link(this.getScope(), type);

    FieldSymbol intParam = MontiArcMill.fieldSymbolBuilder()
      .setName("param")
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .build();

    MethodSymbol instanceMethod =
      createMethod("instanceMethod", false, false, intParam.deepClone(), intParam.deepClone());
    MethodSymbol instanceMethodVarargs = createMethod("instanceMethodVarargs", false, true, intParam.deepClone());
    MethodSymbol staticMethod = createMethod("staticMethod", true, false, intParam.deepClone(), intParam.deepClone());
    MethodSymbol staticMethodVarargs = createMethod("staticMethodVarargs", true, true, intParam.deepClone());

    type.addMethodSymbol(staticMethod);
    type.addMethodSymbol(staticMethodVarargs);
    type.addMethodSymbol(instanceMethod);
    type.addMethodSymbol(instanceMethodVarargs);

    // Create instance of this type
    FieldSymbol instanceWithMethods = MontiArcMill.fieldSymbolBuilder()
      .setName("instanceWithMethods")
      .setType(SymTypeExpressionFactory.createTypeExpression(type))
      .build();
    SymbolService.link(this.getScope(), instanceWithMethods);
  }

  protected MethodSymbol createMethod(@NotNull String name,
                                      boolean isStatic,
                                      boolean isElliptic,
                                      @NotNull FieldSymbol... params) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(params);

    MethodSymbol method = MontiArcMill.methodSymbolBuilder()
      .setName(name)
      .setIsStatic(isStatic)
      .setIsElliptic(isElliptic)
      .setType(SymTypeExpressionFactory.createPrimitive("int"))
      .setSpannedScope(MontiArcMill.scope())
      .build();
    Arrays.stream(params).forEach(p -> SymbolService.link(method.getSpannedScope(), p));

    return method;
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
  public void shouldCalculateObjectTypeFromMethodCalls(@NotNull String expression,
                                                       @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);

    //Given
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(ast, this.getScope());

    //When
    TypeCheckResult result = this.getTypeCalculator().deriveType(ast);

    //Then
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertFalse(result.getResult().isPrimitive());
    Assertions.assertFalse(result.getResult().isGenericType());
    Assertions.assertFalse(result.getResult().isTypeVariable());
    Assertions.assertTrue(!(result.getResult().getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) result.getResult().getTypeInfo()).lazyLoadDelegate() instanceof OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, result.getResult().print());
  }

  @ParameterizedTest
  @MethodSource("expressionProviderWithGenericMethodCalls")
  public void shouldCalculateObjectTypeFromGenericMethodCalls(@NotNull String expression,
                                                              @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);

    //Given
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(ast, this.getScope());

    //When
    TypeCheckResult result = this.getTypeCalculator().deriveType(ast);

    //Then
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertFalse(result.getResult().isPrimitive());
    Assertions.assertTrue(result.getResult().isGenericType());
    Assertions.assertFalse(result.getResult().isTypeVariable());
    Assertions.assertTrue(!(result.getResult().getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) result.getResult().getTypeInfo()).lazyLoadDelegate() instanceof OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, result.getResult().print());
  }

  @ParameterizedTest
  @MethodSource("expressionProviderWithAssignments")
  public void shouldCalculatePrimitiveTypeFromAssignment(@NotNull String expression,
                                                         @NotNull String expectedType) throws IOException {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(expectedType);

    //Given
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(ast, this.getScope());

    //When
    TypeCheckResult result = this.getTypeCalculator().deriveType(ast);

    //Then
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertTrue(result.getResult().isPrimitive());
    Assertions.assertFalse(result.getResult().isGenericType());
    Assertions.assertFalse(result.getResult().isTypeVariable());
    Assertions.assertTrue(!(result.getResult().getTypeInfo() instanceof OOTypeSymbolSurrogate) ||
      !(((OOTypeSymbolSurrogate) result.getResult().getTypeInfo()).lazyLoadDelegate() instanceof OOTypeSymbolSurrogate));
    Assertions.assertEquals(expectedType, result.getResult().print());
  }

  /**
   * Tests that only one error is logged, if a more complex expression contains one illegal subexpression. I.e., if a
   * subexpression leads to a non-calculable composed expression, the composed expression should not log another error
   * which would be redundant information.
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("expressionWithOneIllegalSubExpression")
  public void shouldOnlyLogOneErrorForIllegalSubExpressions(@NotNull String expression) throws IOException {
    Preconditions.checkNotNull(expression);

    // Given
    ASTExpression expr = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(expr, this.getScope());

    // When
    TypeCheckResult result = new MontiArcTypeCalculator().deriveType(expr);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.isPresentResult());
    Assertions.assertTrue(result.getResult().isObscureType(), "Unexpected type: " + result.getResult().print());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("expressionWithIllegalSubExpressions")
  public void shouldEagerlyEvaluateSubExpressions(@NotNull String expression) throws IOException {
    Preconditions.checkNotNull(expression);

    // Given
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(ast, this.getScope());

    // When
    TypeCheckResult result = new MontiArcTypeCalculator().deriveType(ast);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isPresentResult() && !result.getResult().isObscureType(),
      "Unexpected type " + result.getResult().print()
    );
  }

  /**
   * Tests that in the case of a composed subexpression with invalid subexpressions every subexpression is type-checked.
   * However, there is one exception: if a {@link de.monticore.expressions.expressionsbasis._ast.ASTNameExpression} or
   * {@link ASTFieldAccessExpression} represent the qualification of a method name or field, then the qualified name
   * parts will not be traversed by the type check. Therefore, they are unsuitable for being tested by
   * {@link #shouldEagerlyEvaluateSubExpressions(String)} that internally checks that all sub expressions have been
   * traversed.
   */
  @ParameterizedTest(name = "{0}")
  @MethodSource("expressionOnlyWithIllegalSubExpressionsAndQualifiedNameProvider")
  public void shouldEagerlyEvaluateSubExpressionsExceptQualifiedNames(@NotNull String expression) throws IOException {
    Preconditions.checkNotNull(expression);

    // Given
    ASTExpression ast = MontiArcMill.parser().parse_StringExpression(expression).orElseThrow();
    this.getScopeSetter().setScope(ast, this.getScope());

    // When
    TypeCheckResult result = new MontiArcTypeCalculator().deriveType(ast);

    // Then
    Assertions.assertNotNull(result);
    Assertions.assertFalse(result.isPresentResult() && !result.getResult().isObscureType(),
      "Unexpected type " + result.getResult().print()
    );
  }

  @Override
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new MontiArcTypeCalculator();
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
}