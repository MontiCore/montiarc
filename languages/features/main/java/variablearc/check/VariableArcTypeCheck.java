/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis.check.ArcBasisTypeCheck;
import arcbasis.check.ArcBasisWithinTypeBasicSymbolsResolver;
import com.google.common.base.Preconditions;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;

import java.util.Optional;

/**
 * TypeCheck3 implementation for Montiarc. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class VariableArcTypeCheck extends ArcBasisTypeCheck {
  
  private static final String LOG_NAME = VariableArcTypeCheck.class.getSimpleName();

  private static ComponentTypeSymbol currentVariant;

  public static void setCurrentVariant(@Nullable ComponentTypeSymbol variant) {
    Log.trace("Switch the context of the type-check", LOG_NAME);
    currentVariant = variant;
  }

  public static Optional<ComponentTypeSymbol> getCurrentVariant() {
    return Optional.ofNullable(currentVariant);
  }

  /**
   * @see MapBasedTypeCheck3(ITraverser, Type4Ast, InferenceContext4Ast)
   */
  protected VariableArcTypeCheck(ArcBasisTraverser typeTraverser,
                                 Type4Ast type4Ast,
                                 InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }

  public static void init() {
    Log.trace("Start initializing the type-check", LOG_NAME);
    initTC3Delegate();
    Log.trace("Finished initializing the type-check", LOG_NAME);
  }

  protected static void initTC3Delegate() {
    initTC3Delegate(
      VariableArcMill.inheritanceTraverser(),
      new Type4Ast(),
      new InferenceContext4Ast(),
      new VariableArcVariantWithinScopeBasicSymbolsResolver(),
      new ArcBasisWithinTypeBasicSymbolsResolver()
    );
  }

  protected static void initTC3Delegate(@NotNull VariableArcTraverser traverser,
                                        @NotNull Type4Ast type4Ast,
                                        @NotNull InferenceContext4Ast ctx4Ast,
                                        @NotNull WithinScopeBasicSymbolsResolver inScopeResolver,
                                        @NotNull WithinTypeBasicSymbolsResolver inTypeResolver) {
    Preconditions.checkNotNull(traverser);
    Preconditions.checkNotNull(type4Ast);
    Preconditions.checkNotNull(ctx4Ast);
    Preconditions.checkNotNull(inScopeResolver);
    Preconditions.checkNotNull(inTypeResolver);
    Log.trace("Start initializing the type-check delegate", LOG_NAME);
    initTypeVisitors(traverser, type4Ast, ctx4Ast, inScopeResolver, inTypeResolver);
    Log.trace("Set the type-check delegate as global TC3 delegate", LOG_NAME);
    setDelegate(new VariableArcTypeCheck(traverser, type4Ast, ctx4Ast));
    Log.trace("Finish initializing the type-check delegate", LOG_NAME);
  }
}
