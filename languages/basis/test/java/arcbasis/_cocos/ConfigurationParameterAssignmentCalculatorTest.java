/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcArguments;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ArcBasisSymbols2Json;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisArtifactScope;
import arcbasis._symboltable.SymbolService;
import arcbasis.check.ArcBasisTypeCheckTest;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests {@link  ConfigurationParameterAssignment}
 */
class ConfigurationParameterAssignmentCalculatorTest extends ArcBasisTypeCheckTest {
  
  protected static final String PACKAGE = "cocos";
  
  @BeforeEach
  public void setUpCompTypes() {
    Path genericTypePath = Path.of(TEST_RESOURCE, PACKAGE, "ComponentParameters.sym");
    ArcBasisSymbols2Json symbols2Json = new ArcBasisSymbols2Json();
    IArcBasisArtifactScope scope = symbols2Json.load(genericTypePath.toString());
    ArcBasisMill.globalScope().addSubScope(scope);
  }

  @Override
  public void setUpFields() {
    super.setUpFields();

    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName("intVar")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName("booleanVar")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName("doubleVar")
      .setType(SymTypeExpressionFactory.createPrimitive("double")).build();

    SymbolService.link(this.scope, anInt, aBool, aDouble);
  }

  @Test
  void shouldFindTooFewParameterBindingsInInstantiation() {
    // Given: Simple()
    ASTComponentInstantiation compInst = provideInstantiation("Simple");
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    
    // When
    coco.check(compInst.getComponentInstance(0));
    
    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS
    ));
  }

  @Test
  void shouldFindTooManyParameterBindingsInInstantiation() {
    // Given: Simple(int, bool)
    ASTComponentInstantiation compInst = provideInstantiation("Simple", intArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    
    // When
    coco.check(compInst.getComponentInstance(0));
    
    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS
    ));
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsInInstantiation() {
    // Given: Simple(int)
    ASTComponentInstantiation compInst = provideInstantiation("Simple", intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    
    // When
    coco.check(compInst.getComponentInstance(0));
    
    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindTooFewParameterBindingsWithDefaultParametersInInstantiation() {
    // Given: Advanced
    ASTComponentInstantiation compInst = provideInstantiation("Advanced");
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS
    ));
  }

  @Test
  void shouldFindTooManyParameterBindingsWithDefaultParametersInInstantiation() {
    // Given: Advanced(int, bool, double, int)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", intArg(), boolArg(), doubleArg(), intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS
    ));
  }

  @Test
  void shouldFindWrongDefaultParameterOverwriteSequenceInInstantiation() {
    // Given: Advanced(int, double)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", intArg(), doubleArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsWithDefaultParametersInInstantiation() {
    // Given: Advanced(int)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsAllDefaultsOverwrittenInInstantiation() {
    // Given: Advanced(int, bool, double)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", intArg(), boolArg(), doubleArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsSomeDefaultsOverwrittenInInstantiation() {
    // Given: Advanced(int, bool)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", intArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindWrongTypesInInstantiation() {
    // Given: Advanced(double, boolean)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", doubleArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void wrongTypeAndTooManyArgumentsInInstantiation() {
    // Given: Advanced(double, int, boolean, boolean)
    ASTComponentInstantiation compInst = provideInstantiation("Advanced", doubleArg(), intArg(), boolArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH,
      ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void wrongTypeAndTooFewArgumentsInInstantiation() {
    // Given: AdvancedWithOneOptional(double)
    ASTComponentInstantiation compInst = provideInstantiation("AdvancedWithOneOptional", doubleArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(compInst.getComponentInstance(0));

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void shouldFindTooFewParameterBindingsInCompRef() {
    // Given: Simple()
    ASTArcComponentType ref = provideCompWithRefinement("Simple");
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS
    ));
  }

  @Test
  void shouldFindTooManyParameterBindingsInCompRef() {
    // Given: Simple(int, bool)
    ASTArcComponentType ref = provideCompWithRefinement("Simple", intArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS
    ));
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsInCompRef() {
    // Given: Simple(int)
    ASTArcComponentType ref = provideCompWithRefinement("Simple", intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindTooFewParameterBindingsWithDefaultParametersInCompRef() {
    // Given: Advanced()
    ASTArcComponentType ref = provideCompWithRefinement("Advanced");
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS
    ));
  }

  @Test
  void shouldFindTooManyParameterBindingsWithDefaultParametersInCompRef() {
    // Given: Advanced(int, boolean, double, int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced",
      intArg(), boolArg(), doubleArg(), intArg()
    );
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS
    ));
  }

  @Test
  void shouldFindWrongBindingWithDefaultParametersInCompRef() {
    // Given: Advanced(int, double)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg(), doubleArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsWithDefaultParametersInCompRef() {
    // Given: Advanced(int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsAllDefaultsOverwrittenInCompRef() {
    // Given: Advanced(int, boolean, double)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced",
      intArg(), boolArg(), doubleArg()
    );
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindCorrectNumberOfParameterBindingsSomeDefaultsOverwrittenInCompRef() {
    // Given: Advanced(int, boolean)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @Test
  void shouldFindWrongTypesInCompRef() {
    // Given: Advanced(double, boolean)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", doubleArg(), boolArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void wrongTypeAndTooManyArgumentsInCompRef() {
    // Given: Advanced(double, int, boolean, boolean)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced",
      doubleArg(), intArg(), boolArg(), boolArg()
    );
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(getErrorCodes(
      ArcError.TOO_MANY_ARGUMENTS,
      ArcError.COMP_ARG_TYPE_MISMATCH,
      ArcError.COMP_ARG_TYPE_MISMATCH,
      ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void wrongTypeAndTooFewArgumentsInCompRef() {
    // Given: AdvancedWithOneOptional(double)
    ASTArcComponentType ref = provideCompWithRefinement("AdvancedWithOneOptional", doubleArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();

    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH
    ));
  }

  @Test
  void shouldDetectAmbiguousKeys() {
    // Given: Advanced(first = int, first = int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg("first"), intArg("first"));
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.KEY_NOT_UNIQUE
    ));
  }

  @Test
  void shouldDetectMultiAssignment() {
    // Given: Advanced(int, first = int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg(), intArg("first"));
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_MULTIPLE_VALUES
    ));
  }

  @Test
  void shouldDetectKeyTooEarly() {
    // Given: Advanced(int, first = int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg("first"), intArg());
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_VALUE_AFTER_KEY
    ));
  }

  @Test
  void shouldDetectInvalidKey() {
    // Given: Advanced(int, first = int)
    ASTArcComponentType ref = provideCompWithRefinement("Advanced", intArg(), boolArg("noKey"));
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    // When
    coco.check(ref);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(
      ArcError.COMP_ARG_KEY_INVALID
    ));
  }



  /**
   * Instantiates the given component type using the given arguments and gives it the given name.
   */
  protected static ASTComponentInstantiation provideInstantiation(@NotNull String componentType,
                                                                  @NotNull ASTArcArgument... arguments) {
    Preconditions.checkNotNull(componentType);
    Preconditions.checkNotNull(arguments);

    ArcComponentTypeSymbol instantiatedType = ArcBasisMill.globalScope().resolveArcComponentType(componentType).orElseThrow();
    
    CompTypeExpression compExpr = new TypeExprOfComponent(instantiatedType);
    compExpr.addArcArguments(Arrays.asList(arguments));
    
    SubcomponentSymbol sym = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("inst")
      .setEnclosingScope(ArcBasisMill.globalScope())
      .setType(compExpr)
      .build();
    
    ASTComponentInstance inst = ArcBasisMill.componentInstanceBuilder()
      .setName("inst")
      .setArcArguments(Mockito.mock(ASTArcArguments.class))
      .build();
    inst.setSymbol(sym);
    sym.setAstNode(inst);
    
    return ArcBasisMill.componentInstantiationBuilder()
      .setMCType(Mockito.mock(ASTMCType.class))
      .addComponentInstance(inst)
      .build();
  }

  protected ASTArcComponentType provideCompWithRefinement(@NotNull String abstractionName,
                                                          @NotNull ASTArcArgument... args) {
    Preconditions.checkNotNull(abstractionName);
    Preconditions.checkNotNull(args);
    Preconditions.checkArgument(Arrays.stream(args).noneMatch(Objects::isNull));

    ArcComponentTypeSymbol abstraction = ArcBasisMill.globalScope().resolveArcComponentType(abstractionName).orElseThrow();

    CompTypeExpression compExpr = new TypeExprOfComponent(abstraction);
    compExpr.addArcArguments(Arrays.asList(args));

    ArcComponentTypeSymbol concretization = ArcBasisMill.arcComponentTypeSymbolBuilder()
      .setName("Dummy")
      .setSpannedScope(ArcBasisMill.scope())
      .addRefinements(compExpr)
      .build();

    ASTArcComponentType ast = ArcBasisMill.arcComponentTypeBuilder()
      .setName("Dummy")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    ast.setSymbol(concretization);

    return ast;
  }

  protected ASTArcArgument arcArg(@Nullable String key, @NotNull ASTExpression expr) {
    return ArcBasisMill.arcArgumentBuilder().setName(key).setExpression(expr).build();
  }

  protected ASTArcArgument intArg() {
    return arcArg(null, intExpr());
  }

  protected ASTArcArgument doubleArg() {
    return arcArg(null, doubleExpr());
  }

  protected ASTArcArgument boolArg() {
    return arcArg(null, boolExpr());
  }

  protected ASTArcArgument intArg(@NotNull String key) {
    return arcArg(key, intExpr());
  }

  protected ASTArcArgument doubleArg(@NotNull String key) {
    return arcArg(key, doubleExpr());
  }

  protected ASTArcArgument boolArg(@NotNull String key) {
    return arcArg(key, boolExpr());
  }

  protected ASTExpression intExpr() {
    ASTExpression expr = ArcBasisMill.nameExpressionBuilder().setName("intVar").build();
    expr.setEnclosingScope(ArcBasisMill.globalScope());
    return expr;
  }

  protected ASTExpression doubleExpr() {
    ASTExpression expr = ArcBasisMill.nameExpressionBuilder().setName("doubleVar").build();
    expr.setEnclosingScope(ArcBasisMill.globalScope());
    return expr;
  }

  protected ASTExpression boolExpr() {
    ASTExpression expr = ArcBasisMill.nameExpressionBuilder().setName("booleanVar").build();
    expr.setEnclosingScope(ArcBasisMill.globalScope());
    return expr;
  }
}
