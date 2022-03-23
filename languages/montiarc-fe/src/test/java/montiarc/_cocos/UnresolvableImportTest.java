/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.scbasis._symboltable.SCStateSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc.MontiArcMill;
import montiarc._symboltable.IMontiArcScope;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import variablearc._symboltable.ArcFeatureSymbol;

import java.util.stream.Stream;

class UnresolvableImportTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "unresolvableImport";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new UnresolvableImport());
  }

  @Override
  @BeforeEach
  public void init() {
    super.init();
    provideTypes();
  }


  protected void provideTypes() {
    // Add scope 'exists'
    IMontiArcScope existsScope = MontiArcMill.scope();
    existsScope.setName("exists");
    MontiArcMill.globalScope().addSubScope(existsScope);

    // Add subscope 'exists.present'
    IMontiArcScope presentScope = MontiArcMill.scope();
    presentScope.setName("present");
    existsScope.addSubScope(presentScope);

    // Add TypeSymbol 'exists.PresentType'
    TypeSymbol typeSym = MontiArcMill.typeSymbolBuilder()
      .setName("PresentType")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    existsScope.add(typeSym);

    // Add OOTypeSymbol 'exists.PresentOOType'
    OOTypeSymbol ooTypeSym = providePresentOOType();
    existsScope.add(ooTypeSym);
    existsScope.addSubScope(ooTypeSym.getSpannedScope());

    // Add ComponentTypeSymbol 'exists.ComponentType'
    ComponentTypeSymbol compTypeSym = MontiArcMill.componentTypeSymbolBuilder()
      .setName("ComponentType")
      .setSpannedScope(MontiArcMill.scope())
      .build();
    existsScope.add(compTypeSym);

    // Add TypeVarSymbol 'exists.TypeVar'
    TypeVarSymbol typeVarSym = MontiArcMill.typeVarSymbolBuilder()
      .setName("TypeVar")
      .build();
    existsScope.add(typeVarSym);

    // Add PortSymbol 'exists.Port'
    PortSymbol portSym = MontiArcMill.portSymbolBuilder()
      .setName("Port")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIncoming(true)
      .build();
    existsScope.add(portSym);

    // Add ComponentInstanceSymbol 'exists.CompInstance'
    ComponentInstanceSymbol compInstSym = MontiArcMill.componentInstanceSymbolBuilder()
      .setName("CompInstance")
      .build();
    existsScope.add(compInstSym);

    // Add ArcFeatureSymbol 'exists.ArcFeature'
    ArcFeatureSymbol featureSym = MontiArcMill.arcFeatureSymbolBuilder()
      .setName("ArcFeature")
      .build();
    existsScope.add(featureSym);

    // Add SCStateSymbol 'exists.SomeState'
    SCStateSymbol stateSym = MontiArcMill.sCStateSymbolBuilder()
      .setName("SomeState")
      .build();
    existsScope.add(stateSym);
  }

  /**
   * Creates OOType 'PresentOOType' with fields 'instanceField' and 'staticField', and with methods 'instanceMethod' and
   * 'staticMethod'
   */
  protected OOTypeSymbol providePresentOOType() {
    OOTypeSymbol presentType = MontiArcMill.oOTypeSymbolBuilder()
      .setName("PresentOOType")
      .setSpannedScope(MontiArcMill.scope())
      .build();

    presentType.addFieldSymbol(MontiArcMill.fieldSymbolBuilder()
      .setName("instanceField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIsStatic(false)
      .build()
    );

    presentType.addFieldSymbol(MontiArcMill.fieldSymbolBuilder()
      .setName("staticField")
      .setType(Mockito.mock(SymTypeExpression.class))
      .setIsStatic(true)
      .build()
    );

    presentType.addMethodSymbol(MontiArcMill.methodSymbolBuilder()
      .setName("instanceMethod")
      .setReturnType(Mockito.mock(SymTypeExpression.class))
      .setIsStatic(false)
      .build()
    );

    presentType.addMethodSymbol(MontiArcMill.methodSymbolBuilder()
      .setName("staticMethod")
      .setReturnType(Mockito.mock(SymTypeExpression.class))
      .setIsStatic(true)
      .build()
    );

    return presentType;
  }

  @ParameterizedTest
  @ValueSource(strings = {"ResolvableImports.arc"})
  void shouldApproveResolvableImports(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("UnresolvableImports.arc",
        ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT,
        // The following three errors should occur because an imported package could not be resolved. However, we
        // currently do not check for missing packages, thus we deactivate their expected errors.
        //   ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT,
        ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT,
        ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT, ArcError.UNRESOLVABLE_IMPORT,
        ArcError.UNRESOLVABLE_IMPORT)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  void shouldFindInvalidTypes(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}
