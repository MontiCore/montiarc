package de.monticore.lang.montiarc.montiarc._symboltable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.names.CommonQualifiedNamesCalculator;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.se_rwth.commons.logging.Log;

/**
 * Created by Michael von Wenckstern on 23.05.2016.
 * this is only a hack, it must be fixed in the SymbolTable
 *
 * @see <a href="https://sselab.de/lab2/private/trac/MontiArc4/ticket/36">Ticket 36</a>
 */
public class MontiArcArtifactScope extends ArtifactScope {

  private CommonQualifiedNamesCalculator qualifiedNamesCalculator = new CommonQualifiedNamesCalculator();

  public MontiArcArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
  }

  public MontiArcArtifactScope(Optional<MutableScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
  }

  @Override
  protected <T extends Symbol> Collection<T> continueWithEnclosingScope(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    final Collection<T> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosing(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof GlobalScope)) {
        Log.warn("0xA1039 An artifact scope should have the global scope as enclosing scope or no "
            + "enclosing scope at all.");
      }

      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, getPackageName(), getImports());

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<T> resolvedFromEnclosing = enclosingScope.resolveMany(resolvingInfo, potentialQualifiedName, kind, modifier, predicate);

        if (!resolvedFromEnclosing.isEmpty()) {
          return resolvedFromEnclosing;
        }

        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }
}
