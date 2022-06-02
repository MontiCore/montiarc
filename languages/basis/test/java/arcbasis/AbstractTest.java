/* (c) https://github.com/MontiCore/monticore */
package arcbasis;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IArcBasisScope;
import montiarc.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.regex.Pattern;

public abstract class AbstractTest extends montiarc.util.AbstractTest {

  @BeforeEach
  public void init() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    addBasicTypes2Scope();
  }

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  protected static ASTMCQualifiedName createQualifiedName(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedNameBuilder().setPartsList(Arrays.asList(parts)).build();
  }

  protected static ASTMCQualifiedType createQualifiedType(@NotNull String... parts) {
    assert parts != null && !Arrays.asList(parts).contains(null);
    return ArcBasisMill.mCQualifiedTypeBuilder().setMCQualifiedName(createQualifiedName(parts)).build();
  }

  protected static TypeSymbol createTypeSymbol(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return ArcBasisMill.typeSymbolBuilder()
      .setName(name)
      .setSpannedScope(ArcBasisMill.scope())
      .build();
  }

  protected static void createTypeSymbolInScope(@NotNull String typeName, @NotNull String scopeName) {
    Preconditions.checkNotNull(typeName);
    Preconditions.checkNotNull(scopeName);

    IArcBasisScope scope = ArcBasisMill.scope();
    scope.setName(scopeName);

    TypeSymbol type = createTypeSymbol(typeName);
    scope.add(type);
    scope.addSubScope(type.getSpannedScope());
    ArcBasisMill.globalScope().addSubScope(scope);
  }

  protected static void createTypeSymbolInGlobalScope(@NotNull String typeName) {
    Preconditions.checkNotNull(typeName);

    TypeSymbol type = createTypeSymbol(typeName);
    ArcBasisMill.globalScope().add(type);
    ArcBasisMill.globalScope().addSubScope(type.getSpannedScope());
  }

  /**
   * Creates an empty ASTComponentType that is linked to a ComponentTypeSymbol with the same name. Beware that the
   * ComponentTypeSymbol is not part of any scope.
   */
  protected static ASTComponentType createComponentTypeWithSymbol(@NotNull String name4Comp) {
    Preconditions.checkNotNull(name4Comp);

    ASTComponentType comp = ArcBasisMill.componentTypeBuilder()
      .setName(name4Comp)
      .setHead(ArcBasisMill.componentHeadBuilder().build())
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .build();

    ComponentTypeSymbol sym = ArcBasisMill.componentTypeSymbolBuilder()
      .setName(name4Comp)
      .setSpannedScope(ArcBasisMill.scope())
      .build();

    comp.setSymbol(sym);
    comp.setSpannedScope(sym.getSpannedScope());
    sym.setAstNode(comp);

    return comp;
  }
}