/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcautomaton._symboltable.Port2EventDefAdapter;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symboltable.ISymbol;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * The symbols the type check currently does not resolve names to, e.g., to
 * type an expression for one choice among the same-named symbols of
 * mutually exclusive variation points. Changing the selection invalidates
 * the types derived for the affected expressions, so these are reset.
 */
public class SymbolSelection {

  protected final Type4Ast type4Ast;
  protected final InferenceContext4Ast ctx4Ast;
  protected Set<ISymbol> excluded = Set.of();

  public SymbolSelection(@NotNull Type4Ast type4Ast, @NotNull InferenceContext4Ast ctx4Ast) {
    this.type4Ast = Preconditions.checkNotNull(type4Ast);
    this.ctx4Ast = Preconditions.checkNotNull(ctx4Ast);
  }

  public boolean isExcluded(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    return this.excluded.contains(this.unwrap(symbol));
  }

  /**
   * Runs with the given symbols excluded from name resolution. The types of
   * the given nodes are derived anew within the run and reset afterward.
   */
  public void within(@NotNull Set<? extends ISymbol> excluded,
                     @NotNull Collection<? extends ASTNode> roots,
                     @NotNull Runnable run) {
    Preconditions.checkNotNull(excluded);
    Preconditions.checkNotNull(roots);
    Preconditions.checkNotNull(run);
    Set<ISymbol> outer = this.excluded;
    Set<ISymbol> selection = Collections.newSetFromMap(new IdentityHashMap<>());
    excluded.stream().map(this::unwrap).forEach(selection::add);
    this.excluded = selection;
    this.reset(roots);
    try {
      run.run();
    } finally {
      this.reset(roots);
      this.excluded = outer;
    }
  }

  protected void reset(@NotNull Collection<? extends ASTNode> roots) {
    for (ASTNode root : roots) {
      this.type4Ast.reset(root);
      this.ctx4Ast.resetContexts(root);
    }
  }

  protected ISymbol unwrap(@NotNull ISymbol symbol) {
    if (symbol instanceof Port2VariableAdapter adapter) {
      return adapter.getAdaptee();
    } else if (symbol instanceof Port2EventDefAdapter adapter) {
      return adapter.getAdaptee();
    }
    return symbol;
  }
}
