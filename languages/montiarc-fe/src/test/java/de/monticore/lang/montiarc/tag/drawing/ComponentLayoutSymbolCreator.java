package de.monticore.lang.montiarc.tag.drawing;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.monticore.lang.montiarc.montiarc._symboltable.ExpandedComponentInstanceSymbol;
import de.monticore.lang.montiarc.tagging._ast.ASTNameScope;
import de.monticore.lang.montiarc.tagging._ast.ASTScope;
import de.monticore.lang.montiarc.tagging._ast.ASTTag;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

/**
 * Created by MichaelvonWenckstern on 04.06.2016.
 */
public class ComponentLayoutSymbolCreator implements TagSymbolCreator {

  /**
   * regular expression pattern:
   * id = {id} , pos = ({x} , {y}) , size = ({width} , {height}) ,
   * layoutPosition = {layoutPosition} , reservedHorizontalSpace = {reservedHorizontalSpace}
   * ( , isOnTop)?
   * to test the pattern just enter:
   * \s*id\s*=\s*([1-9]\d*)\s*,\s*pos\s*=\s*\(([1-9]\d*)\s*,\s*([1-9]\d*)\)\s*,
   * \s*size\s*=\s*\(([1-9]\d*)\s*,\s*([1-9]\d*)\)\s*,\s*layoutPosition\s*=
   * \s*([1-9]\d*)\s*,\s*reservedHorizontalSpace\s*=\s*([1-9]\d*)\s*(?:,\s*(isOnTop))?\s*
   * at http://www.regexplanet.com/advanced/java/index.html
   */
  public static final Pattern pattern = Pattern.compile(
      "\\{\\s*id\\s*=\\s*([1-9]\\d*)\\s*,\\s*pos\\s*=\\s*\\(([1-9]\\d*)\\s*,\\s*"
          + "([1-9]\\d*)\\)\\s*,\\s*size\\s*=\\s*\\(([1-9]\\d*)\\s*,\\s*([1-9]"
          + "\\d*)\\)\\s*,\\s*layoutPosition\\s*=\\s*([1-9]\\d*)\\s*,\\s*"
          + "reservedHorizontalSpace\\s*=\\s*([1-9]\\d*)\\s*(?:,\\s*(isOnTop))?\\s*\\}");

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
        .filter(n -> n.endsWith("LayoutTagSchema"))
        .count() == 0) {
      return; // the tagging model is not conform to the traceability tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackage());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModel().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModel().get())
                .getQualifiedName().toString()) :
            packageName;

    for (ASTTag element : unit.getTagBody().getTags()) {
        element.getTagElements().stream()
            .filter(t -> t.getName().equals("ComponentLayout")) // after that point we can throw error messages
            .filter(t -> t.getTagValue().isPresent())
            .map(t -> matchRegexPattern(t.getTagValue().get()))
            .filter(r -> r != null)
            .forEachOrdered(m ->
                element.getScopes().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> getGlobalScope(gs).<ExpandedComponentInstanceSymbol>
                        resolveDown(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
                        s.getQualifiedName().toString()), ExpandedComponentInstanceSymbol.KIND))
                    .filter(Optional::isPresent) // if the symbol is not present, does not mean that the symbol
                    .map(Optional::get)          // is not available at all, maybe it will be loaded later
                    .forEachOrdered(s -> s.addTag(
                        new ComponentLayoutSymbol(
                            Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)),
                            Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)),
                            m.group(8) != null, Integer.parseInt(m.group(7)))))
            );
    }
  }

  protected Matcher matchRegexPattern(String regex) {
    Matcher matcher = pattern.matcher(regex);
    if (matcher.matches()) {
      return matcher;
    }
    Log.error(String.format("'%s' does not match the specified regex pattern '%s'",
        regex,
        "id = {id} , pos = ({x} , {y}) , size = ({width} , {height}) ,"
            + "layoutPosition = {layoutPosition} , reservedHorizontalSpace = {reservedHorizontalSpace}"
            + "( , isOnTop)?"));
    return null;
  }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("NameScope")) {
      return true;
    }
    Log.error(String.format("Invalid scope kind: '%s'. ComponentLayout expects as scope kind 'NameScope'.", scope.getScopeKind()));
    return false;
  }
}
