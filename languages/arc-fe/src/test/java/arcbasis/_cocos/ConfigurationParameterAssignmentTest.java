/* (c) https://github.com/MontiCore/monticore */

package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.*;
import arcbasis._symboltable.ArcBasisScopesGenitorDelegator;
import arcbasis._symboltable.ArcBasisSymbolTableCompleterDelegator;
import arcbasis.check.ArcBasisDeriveTypeTest;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests {@link  ConfigurationParameterAssignment}
 */
public class ConfigurationParameterAssignmentTest extends ArcBasisDeriveTypeTest {

  protected ArcBasisScopesGenitorDelegator scopeGenitor;
  protected ArcBasisSymbolTableCompleterDelegator completer;

  @Override
  public void setUp() {
    super.setUp();
    this.scopeGenitor = ArcBasisMill.scopesGenitorDelegator();
    this.completer = ArcBasisMill.symbolTableCompleterDelegator();
  }

  @Override
  public void setUpFields() {
    super.setUpFields();

    FieldSymbol anInt = ArcBasisMill.fieldSymbolBuilder().setName("anInt")
      .setType(SymTypeExpressionFactory.createTypeConstant("int")).build();
    FieldSymbol aBool = ArcBasisMill.fieldSymbolBuilder().setName("aBool")
      .setType(SymTypeExpressionFactory.createTypeConstant("boolean")).build();
    FieldSymbol aDouble = ArcBasisMill.fieldSymbolBuilder().setName("aDouble")
      .setType(SymTypeExpressionFactory.createTypeConstant("double")).build();

    this.add2Scope(this.scope, anInt, aBool, aDouble);
  }

  @Test
  public void shouldFindTooFewParameterBindings() {
    ASTComponentType compType = provideSimpleCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(ArcBasisMill.argumentsBuilder().build())
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  @Test
  public void shouldFindTooManyParameterBindings() {
    ASTComponentType compType = provideSimpleCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .addExpression(doBuildNameExpressionInScope("aBool"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment(getDerive());
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  @Test
  public void shouldFindCorrectNumberOfParameterBindings() {
    ASTComponentType compType = provideSimpleCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment(getDerive());
    coco.check(compInst.getComponentInstance(0));

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindTooFewParameterBindingsWithDefaultParameters() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(ArcBasisMill.argumentsBuilder().build())
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  @Test
  public void shouldFindTooManyParameterBindingsWithDefaultParameters() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .addExpression(doBuildNameExpressionInScope("aBool"))
      .addExpression(doBuildNameExpressionInScope("aDouble"))
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  @Test
  public void shouldFindWrongDefaultParameterOverwriteSequence() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .addExpression(doBuildNameExpressionInScope("aDouble"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  @Test
  public void shouldFindCorrectNumberOfParameterBindingsWithDefaultParameters() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectNumberOfParameterBindingsWithAllDefaultsOverwritten() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .addExpression(doBuildNameExpressionInScope("aBool"))
      .addExpression(doBuildNameExpressionInScope("aDouble"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindCorrectNumberOfParameterBindingsWithSomeDefaultsOverwritten() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("anInt"))
      .addExpression(doBuildNameExpressionInScope("aBool"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void shouldFindWrongTypes() {
    ASTComponentType compType = provideAdvancedCompType();
    this.scopeGenitor.createFromAST(compType);
    this.completer.createFromAST(compType);

    ASTArguments arguments = ArcBasisMill.argumentsBuilder()
      .addExpression(doBuildNameExpressionInScope("aDouble"))
      .addExpression(doBuildNameExpressionInScope("aBool"))
      .build();

    // ↓ test subject
    ASTComponentInstantiation compInst = ArcBasisMill.componentInstantiationBuilder()
      .setMCType(mcTypeFromCompType(compType))
      .addComponentInstance(
        ArcBasisMill.componentInstanceBuilder()
          .setName("foo")
          .setArguments(arguments)
          .build())
      .build();
    // ↑ test subject

    ASTComponentType enclComp = encloseInstInCompType(compInst);
    this.scopeGenitor.createFromAST(enclComp);
    this.completer.createFromAST(enclComp);

    ConfigurationParameterAssignment coco = new ConfigurationParameterAssignment();
    coco.check(compInst.getComponentInstance(0));

    this.checkOnlyExpectedErrorsPresent(new ArcError[]{ArcError.CONFIG_PARAMETER_BINDING});
  }

  /**
   * @return An ASTComponentType with one int parameter that has no default value.
   */
  protected ASTComponentType provideSimpleCompType() {
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
  protected ASTComponentType provideAdvancedCompType() {
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
   * Creates an ASTMCType that represents the  given component type.
   *
   * @param comp the component type for which the ASMCType should be created
   * @return the given component type, represented as MCType
   */
  protected ASTMCType mcTypeFromCompType(@NotNull ASTComponentType comp) {
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

  protected ASTComponentType encloseInstInCompType(@NotNull ASTComponentInstantiation inst) {
    Preconditions.checkNotNull(inst);

    return ArcBasisMill.componentTypeBuilder()
      .setName("Outer")
      .setHead(Mockito.mock(ASTComponentHead.class))
      .setBody(ArcBasisMill.componentBodyBuilder().addArcElement(inst).build())
      .build();
  }

  protected ASTExpression doBuildNameExpressionInScope(@NotNull String expression) {
    Preconditions.checkNotNull(expression);
    ASTExpression result = this.doBuildNameExpression(expression);
    this.getScopeSetter().handle(result);
    return result;
  }
}
