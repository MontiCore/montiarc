${tc.signature("packageName", "schemaName", "tagTypeName", "importSymbols", "scopeSymbol", "nameScopeType", "dataType", "isUnit")}

package ${packageName}.${schemaName};

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import ${importSymbols};
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.tagging._ast.ASTNameScope;
import de.monticore.lang.montiarc.tagging._ast.ASTScope;
import de.monticore.lang.montiarc.tagging._ast.ASTTag;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import de.monticore.lang.montiarc.tagvalue._parser.TagValueParser;
  <#if isUnit>
import javax.measure.quantity.${dataType};
import javax.measure.unit.Unit;
import de.monticore.lang.montiarc.tagging.helper.NumericLiteral;
import de.monticore.lang.montiarc.tagvalue._ast.ASTUnitTagValue;
  <#elseif dataType = "String">
import de.monticore.lang.montiarc.tagvalue._ast.ASTStringTagValue;
  <#elseif dataType = "Boolean">
import de.monticore.lang.montiarc.tagvalue._ast.ASTBooleanTagValue;
  <#else>
import de.monticore.lang.montiarc.tagging.helper.NumericLiteral;
import de.monticore.lang.montiarc.tagvalue._ast.ASTNumericTagValue;
  </#if>

/**
 * created by ValuedTagTypeCreator.ftl
 */
public class ${tagTypeName}SymbolCreator implements TagSymbolCreator {

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
            Joiners.DOT.join(packageName,
                unit.getTagBody().getTargetModel().get()
                .getQualifiedNameString()) :
            packageName;

    for (ASTTag element : unit.getTagBody().getTags()) {
      element.getTagElements().stream()
          .filter(t -> t.getName().equals("${tagTypeName}"))
          .filter(t -> t.getTagValue().isPresent())
          .map(t -> checkContent(t.getTagValue().get()))
          .filter(v -> v != null)
        <#if isUnit>
          .filter(this::checkUnit)
        </#if>
          .forEachOrdered(v ->
              element.getScopes().stream()
                .filter(this::checkScope)
                .map(s -> (ASTNameScope) s)
                .map(s -> getGlobalScope(gs).<Symbol>resolveDownMany(
                    Joiners.DOT.join(rootCmp, s.getQualifiedName().toString()),
                    SymbolKind.KIND))
                .filter(s -> !s.isEmpty())
                .map(this::checkKind)
                .filter(s -> s != null)
              <#if isUnit>
                .forEachOrdered(s -> s.addTag(new ${tagTypeName}Symbol(
                   NumericLiteral.getValue(v.getNumericLiteral()),
                   Unit.valueOf(v.getUnit()).asType(${dataType}.class)))));
              <#else>
                .forEachOrdered(s -> s.addTag(new ${tagTypeName}Symbol(v))));
              </#if>
    }
  }

<#if isUnit>
  protected boolean checkUnit(ASTUnitTagValue unitTag) {
    Unit unit;
    try {
      unit = Unit.valueOf(unitTag.getUnit());
    } catch (IllegalArgumentException e) {
      Log.error(String.format("0xT0003 Could not parse unit '%s'. This unit is not supported. All supported units are %s and %s",
          unitTag.getUnit(), "http://jscience.org/api/javax/measure/unit/SI.html",
          "http://jscience.org/api/javax/measure/unit/NonSI.html"), e);
      return false;
    }
    if (!unit.isCompatible(${dataType}.UNIT)) {
      Log.error(String.format("0xT0002 The unit '%s' is not compatible to quantity kind '${dataType}'",
          unitTag.getUnit()));
      return false;
    }
    return true;
  }

  protected ASTUnitTagValue checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTUnitTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parseString_UnitTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse '%s' with TagValueParser#parseString_UnitTagValue",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return ast.get();
  }
<#elseif dataType = "String">
  protected String checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTStringTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parseString_StringTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse %s with TagValueParser#parseString_StringTagValue.",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return ast.get().getString();
  }
<#elseif dataType = "Boolean">
  protected Boolean checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTBooleanTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parseString_BooleanTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse %s with TagValueParser#parseString_BooleanTagValue.",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return ast.get().getT().isPresent();
  }
<#else>
  protected Number checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTNumericTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parseString_NumericTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse %s with TagValueParser#parseString_NumericTagValue.",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return NumericLiteral.getValue(ast.get().getNumericLiteral());
  }
</#if>

  protected ${scopeSymbol} checkKind(Collection<Symbol> symbols) {
    ${scopeSymbol} ret = null;
    for (Symbol symbol : symbols) {
      if (symbol.getKind().isSame(${scopeSymbol}.KIND)) {
        if (ret != null) {
          Log.error(String.format("0xA4095 Found more than one symbol: '%s' and '%s'",
              ret, symbol));
          return null;
        }
        ret = (${scopeSymbol})symbol;
      }
    }
    if (ret == null) {
      Log.error(String.format("0xT0001 Invalid symbol kinds: %s. tagTypeName expects as symbol kind '${scopeSymbol}.KIND'.",
          symbols.stream().map(s -> "'" + s.getKind().toString() + "'").collect(Collectors.joining(", "))));
      return null;
    }
    return ret;
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