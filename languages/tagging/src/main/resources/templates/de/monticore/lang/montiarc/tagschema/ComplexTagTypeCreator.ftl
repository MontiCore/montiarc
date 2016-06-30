${tc.signature("packageName", "schemaName", "tagTypeName", "importSymbols", "scopeSymbol", "nameScopeType",
  "regexPattern", "commentRegexPattern", "symbolParams")}

package ${packageName}.${schemaName};

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ${importSymbols};
import de.monticore.lang.montiarc.tagging._ast.ASTNameScope;
import de.monticore.lang.montiarc.tagging._ast.ASTScope;
import de.monticore.lang.montiarc.tagging._ast.ASTTag;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

/**
 * created by ComplexTagTypeCreator.ftl
 */
public class ${tagTypeName}SymbolCreator implements TagSymbolCreator {

  /**
   * regular expression pattern for:
   * ${commentRegexPattern}
   *
   * the pattern can be tested online at:
   * http://www.regexplanet.com/advanced/java/index.html
  /*
  public static final Pattern pattern = Pattern.compile(
    ${regexPattern}
  );

  public static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }

  public void create(ASTTaggingUnit unit, Scope gs) {
    if (unit.getQualifiedNames().stream()
        .map(q -> q.toString())
        .filter(n -> n.endsWith("${schemaName}"))
        .count() == 0) {
      return; // the tagging model is not conform to the ${schemaName} tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackage());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModel().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModel().get())
                .getQualifiedName().toString()) :
            packageName;

     for (ASTTag element : unit.getTagBody().getTags()) {
            element.getTagElements().stream()
              .filter(t -> t.getName().equals("${tagTypeName}"))
              .filter(t -> !t.getTagValue().isPresent())
              .map(t -> matchRegexPattern(t.getTagValue().get()))
              .filter(r -> r != null)
              .forEachOrdered(m ->
                  element.getScopes().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> getGlobalScope(gs).<${scopeSymbol}>resolveDown(
                        Joiners.DOT.join(rootCmp, s.getQualifiedName().toString()),
                        ${scopeSymbol}.KIND))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEachOrdered(s -> s.addTag(
                        new ${tagTypeName}Symbol(
                          ${symbolParams}
                        ))));
      }
  }

  protected Matcher matchRegexPattern(String regex) {
    Matcher matcher = pattern.matcher(regex);
    if (matcher.matches()) {
      return matcher;
    }
    Log.error(String.format("'%s' does not match the specified regex pattern '%s'",
        regex,
        "${commentRegexPattern}"
    ));
    return null;
  }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("${nameScopeType}")) {
      return true;
    }
    Log.error(String.format("0xT0005 Invalid scope kind: '%s'. ${tagTypeName} expects as scope kind '${nameScopeType}'.",
        scope.getScopeKind()), scope.get_SourcePositionStart());
    return false;
  }
}