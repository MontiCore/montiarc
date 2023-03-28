/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArguments;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import arcbasis._symboltable.SymbolService;
import arcbasis._symboltable.TransitiveScopeSetter;
import arcbasis.check.ArcBasisTypeCalculator;
import arcbasis.check.ArcBasisTypeCalculatorTest;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeRelations;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Tests {@link  ConfigurationParameterAssignment}
 */
public class ConfigurationParameterAssignmentCalculatorTest extends ArcBasisTypeCalculatorTest {

  protected ArcBasisScopesGenitorDelegator scopeGenitor;
  protected ArcBasisSymbolTableCompleterDelegator completer;
  protected final static TransitiveScopeSetter scopeSetter = new TransitiveScopeSetter();

  @Override
  public void setUpScope() {
    super.setUpScope();
    this.scopeGenitor = ArcBasisMill.scopesGenitorDelegator();
    this.completer = ArcBasisMill.symbolTableCompleterDelegator();
  }

  @Override
  public void setUpFields() {
    super.setUpFields();

    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName("anInt")
      .setType(SymTypeExpressionFactory.createPrimitive("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName("aBool")
      .setType(SymTypeExpressionFactory.createPrimitive("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName("aDouble")
      .setType(SymTypeExpressionFactory.createPrimitive("double")).build();

    SymbolService.link(this.scope, anInt, aBool, aDouble);
  }

  @ParameterizedTest(name = "{index}: {0}")
  @MethodSource("provideArgsAndExpectedErrors")
  public void shouldCheckParameterBindings(@NotNull String testName,
                                    @NotNull ASTComponentType toInstantiate,
                                    @NotNull ASTArcArguments instantiationArgs,
                                    @NotNull ArcError... expectedErrors) {
    Preconditions.checkNotNull(testName);
    Preconditions.checkNotNull(toInstantiate);
    Preconditions.checkNotNull(instantiationArgs);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    this.scopeGenitor.createFromAST(toInstantiate);
    this.completer.createFromAST(toInstantiate);

    ASTComponentInstantiation compInst = provideInstantiation(toInstantiate, "foo", instantiationArgs);
    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    // When
    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment(new ArcBasisTypeCalculator(), new TypeRelations());
    coco.check(compInst.getComponentInstance(0));

    // Then
    this.checkOnlyExpectedErrorsPresent(expectedErrors);
  }

  protected static Stream<Arguments> provideArgsAndExpectedErrors() {
    return Stream.of(
      Arguments.arguments("shouldFindTooFewParameterBindings",
        provideSimpleCompType(),
        ArcBasisMill.arcArgumentsBuilder().build(),
        new ArcError[] {ArcError.TOO_FEW_ARGUMENTS}),

      Arguments.arguments("shouldFindTooManyParameterBindings",
        provideSimpleCompType(),
        ArcBasisMill.arcArgumentsBuilder()
          .addArcArgument(ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          ).addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("aBool")
            ).build()
          ).build(),
        new ArcError[] {ArcError.TOO_MANY_ARGUMENTS}),

      Arguments.arguments("shouldFindCorrectNumberOfParameterBindings",
        provideSimpleCompType(),
        ArcBasisMill.arcArgumentsBuilder()
          .addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          ).build(),
        new ArcError[]{}),

      Arguments.arguments("shouldFindTooFewParameterBindingsWithDefaultParameters",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().build(),
        new ArcError[] {ArcError.TOO_FEW_ARGUMENTS}),

      Arguments.arguments("shouldFindTooManyParameterBindingsWithDefaultParameters",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          ).addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("aBool")
            ).build()
          ).addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("aDouble")
            ).build()
          ).addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          ).build(),
        new ArcError[] {ArcError.TOO_MANY_ARGUMENTS}),

      Arguments.arguments("shouldFindWrongDefaultParameterOverwriteSequence",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          ).addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("aDouble")
            ).build()
          ).build(),
        new ArcError[] {ArcError.COMP_ARG_TYPE_MISMATCH}),

      Arguments.arguments("shouldFindCorrectNumberOfParameterBindingsWithDefaultParameters",
        provideAdvancedCompType(), ArcBasisMill.arcArgumentsBuilder().addArcArgument(
            ArcBasisMill.arcArgumentBuilder().setExpression(
              doBuildNameExpressionInScope("anInt")
            ).build()
          )
          .build(),
        new ArcError[]{}),

      Arguments.arguments("shouldFindCorrectNumberOfParameterBindingsAllDefaultsOverwritten",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("anInt")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aBool")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aDouble")
          ).build()
        ).build(),
        new ArcError[]{}),

      Arguments.arguments("shouldFindCorrectNumberOfParameterBindingsSomeDefaultsOverwritten",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("anInt")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aBool")
          ).build()
        ).build(),
        new ArcError[]{}),

      Arguments.arguments("shouldFindWrongTypes",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aDouble")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aBool")
          ).build()
        ).build(),
        new ArcError[] {ArcError.COMP_ARG_TYPE_MISMATCH}),

      Arguments.arguments("wrongTypeAndTooManyArguments",
        provideAdvancedCompType(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aDouble")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("anInt")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aBool")
          ).build()
        ).addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aBool")
          ).build()
        ).build(),
        new ArcError[] {ArcError.TOO_MANY_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH,
          ArcError.COMP_ARG_TYPE_MISMATCH, ArcError.COMP_ARG_TYPE_MISMATCH}),

      Arguments.arguments("wrongTypeAndTooFewArguments",
        provideAdvancedCompTypeWithOneDefaultValue(),
        ArcBasisMill.arcArgumentsBuilder().addArcArgument(
          ArcBasisMill.arcArgumentBuilder().setExpression(
            doBuildNameExpressionInScope("aDouble")
          ).build()
        ).build(),
        new ArcError[] {ArcError.TOO_FEW_ARGUMENTS, ArcError.COMP_ARG_TYPE_MISMATCH}),

    Arguments.arguments("shouldFindTypeReference",
      provideSimpleCompType(),
      ArcBasisMill.arcArgumentsBuilder().addArcArgument(
        ArcBasisMill.arcArgumentBuilder().setExpression(
          doBuildNameExpressionInScope("Person")
        ).build()
      ).build(),
      new ArcError[]{ArcError.TYPE_REF_NO_EXPRESSION})
    );
  }

  /**
   * @return An ASTComponentType with one int parameter that has no default value.
   */
  protected static ASTComponentType provideSimpleCompType() {
    ASTComponentHead compHead = ArcBasisMill.componentHeadBuilder()
      .addArcParameter(
        ArcBasisMill.arcParameterBuilder()
          .setName("intParam")
          .setMCType(createQualifiedType("int"))
          .build())
      .build();

    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("SimpleCompType")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(compHead)
      .build();

    return compType;
  }

  /**
   * @return An ASTComponentType with an int parameter that has no default value, followed by a boolean and double
   * parameter with default values.
   */
  protected static ASTComponentType provideAdvancedCompType() {
    ASTArcParameter param1 = ArcBasisMill.arcParameterBuilder()
      .setName("first")
      .setMCType(createQualifiedType("int"))
      .build();
    ASTArcParameter param2 = ArcBasisMill.arcParameterBuilder()
      .setName("second")
      .setMCType(createQualifiedType("boolean"))
      .setDefault(doBuildNameExpressionInScope("aBool"))
      .build();
    ASTArcParameter param3 = ArcBasisMill.arcParameterBuilder()
      .setName("third")
      .setMCType(createQualifiedType("double"))
      .setDefault(doBuildNameExpressionInScope("aDouble"))
      .build();

    ASTComponentHead compHead = ArcBasisMill.componentHeadBuilder()
      .addArcParameter(param1)
      .addArcParameter(param2)
      .addArcParameter(param3)
      .build();

    ASTComponentType compType = ArcBasisMill.componentTypeBuilder()
      .setName("AdvancedCompType")
      .setBody(Mockito.mock(ASTComponentBody.class))
      .setHead(compHead)
      .build();

    return compType;
  }

  /**
   * Like {@link #provideAdvancedCompType()} but the second parameter is mandatory.
   */
  protected static ASTComponentType provideAdvancedCompTypeWithOneDefaultValue() {
    ASTComponentType compType = provideAdvancedCompType();
    compType.getHead().getArcParameter(1).setDefaultAbsent();
    return compType;
  }

  /**
   * Instantiates the given component type using the given arguments and gives it the given name.
   */
  protected static ASTComponentInstantiation provideInstantiation(@NotNull ASTComponentType componentType,
                                                           @NotNull String name,
                                                           @NotNull ASTArcArguments arguments) {
    Preconditions.checkNotNull(componentType);
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(arguments);

    return ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(componentType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName(name)
          .setArcArguments(arguments)
          .build())
      .build();
  }


  /**
   * Creates an ASTMCType that represents the  given component type.
   *
   * @param comp the component type for which the ASMCType should be created
   * @return the given component type, represented as MCType
   */
  protected static ASTMCType mcTypeFromCompType(@NotNull ASTComponentType comp) {
    Preconditions.checkNotNull(comp);
    Preconditions.checkNotNull(comp.getEnclosingScope());

    ASTMCType type = ArcBasisMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(
        ArcBasisMill.mCQualifiedNameBuilder()
          .addParts(comp.getName())
          .build())
      .build();
    type.setEnclosingScope(comp.getEnclosingScope());

    return type;
  }

  protected static ASTComponentType encloseInstInCompType(@NotNull ASTComponentInstantiation inst) {
    Preconditions.checkNotNull(inst);

    return ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder().addArcElement(inst).build())
      .build();
  }

  protected static ASTExpression doBuildNameExpressionInScope(@NotNull String expression) {
    Preconditions.checkNotNull(expression);
    ASTExpression result = doBuildNameExpression(expression);
    scopeSetter.setScope(result, ArcBasisMill.globalScope());
    return result;
  }
}
