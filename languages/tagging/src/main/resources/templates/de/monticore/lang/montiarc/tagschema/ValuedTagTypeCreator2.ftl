${tc.signature("packageName", "schemaName", "tagTypeName", "importSymbols", "scopeSymbol", "nameScopeType", "dataType", "isUnit")}

package ${packageName}.${schemaName};

import java.util.Optional;

import ${importSymbols};
import de.monticore.lang.montiarc.tagging._ast.ASTNameScope;
import de.monticore.lang.montiarc.tagging._ast.ASTScope;
import de.monticore.lang.montiarc.tagging._ast.ASTTag;
import de.monticore.lang.montiarc.tagging._ast.ASTTagElement;
import de.monticore.lang.montiarc.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.montiarc.tagging._ast.ASTTargetElement;
import de.monticore.lang.montiarc.tagging._ast.ASTValuedTag;
import de.monticore.lang.montiarc.tagging._symboltable.TagSymbolCreator;
import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNumericLiteral;
import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals.literals._ast.ASTSignedLongLiteral;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
  <#if isUnit>
import de.monticore.lang.montiarc.unit.*;
import de.monticore.lang.montiarc.tagging._ast.ASTUnitTagValue;
  <#elseif dataType = "String">
import de.monticore.lang.montiarc.tagging._ast.ASTStringTagValue;
  <#elseif dataType = "Boolean">
import de.monticore.lang.montiarc.tagging._ast.ASTBooleanTagValue;
  <#else>
import de.monticore.lang.montiarc.tagging._ast.ASTNumericTagValue;
  </#if>

/**
 * created by SimpleTagTypeCreator.ftl
 */
public class ${tagTypeName}SymbolCreator implements TagSymbolCreator {
<#if isUnit || dataType != "String">
  // TODO ASTNumericLiteral should have a getValue() method
  protected static double getValue(ASTNumericLiteral numericLiteral) {
    if (numericLiteral instanceof ASTDoubleLiteral) {
      return ((ASTDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedDoubleLiteral) {
      return ((ASTSignedDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTIntLiteral) {
      return ((ASTIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedIntLiteral) {
      return ((ASTSignedIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTFloatLiteral) {
      return ((ASTFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedFloatLiteral) {
      return ((ASTSignedFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTLongLiteral) {
      return ((ASTLongLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedLongLiteral) {
      return ((ASTSignedLongLiteral) numericLiteral).getValue();
    }
    else {
      throw new Error("unexpected ASTNumericLiteral: " + numericLiteral.getClass());
    }
  }
</#if>

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

    for (ASTTagElement element : unit.getTagBody().getTagElements()) {
      if (element instanceof ASTTargetElement) {
          ((ASTTargetElement) element).getTags().stream()
              .filter(t -> t.getName().equals("${tagTypeName}"))
              .filter(this::checkASTTagKind)
              .map(t -> (ASTValuedTag) t)
              .filter(this::checkValueKind)
            <#if isUnit>
              .map(v -> (ASTUnitTagValue) v.getTagValue())
              .filter(this::checkUnitKind)
            <#elseif dataType = "String">
              .map(v -> (ASTStringTagValue)v.getTagValue())
            <#elseif dataType = "Boolean">
              .map(t -> (ASTBooleanTagValue)t.getTagValue())
            <#else>
              .map(v -> (ASTNumericTagValue)v.getTagValue())
            </#if>
              .forEachOrdered(v ->
                  ((ASTTargetElement) element).getScopes().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> getGlobalScope(gs).<${scopeSymbol}>resolveDown(
                        Joiners.DOT.join(rootCmp, s.getQualifiedName().toString()),
                        ${scopeSymbol}.KIND))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                  <#if isUnit>
                    .forEachOrdered(s -> s.addTag(new ${tagTypeName}Symbol(getValue(v.getNumericLiteral()),
                                            Units.get${dataType}(v.getUnit()).get()))));
                  <#elseif dataType = "String">
                    .forEachOrdered(s -> s.addTag(new ${tagTypeName}Symbol(v.getString()))));
                  <#elseif dataType = "Boolean">
                    .forEachOrdered(s -> s.addTag(new PowerBooleanSymbol(v.getT().isPresent()))));
                  <#else>
                    .forEachOrdered(s -> s.addTag(new PowerIdSymbol((int)getValue(v.getNumericLiteral())))));
                  </#if>
      }
    }
  }

  protected boolean checkASTTagKind(ASTTag tag) {
    if (tag.getTagKind().equals("ValuedTag")) {
      return true;
    }
    Log.error(String.format("0xT0001 Invalid tag kind: '%s'. ${tagTypeName} expects as tag kind 'ValuedTag'.",
        tag.getTagKind()), tag.get_SourcePositionStart());
    return false;
  }

<#if isUnit>
  protected boolean checkUnitKind(ASTUnitTagValue unit) {
    if (Units.get${dataType}(unit.getUnit()).isPresent()) {
      return true;
    }
    Optional<Unit> u = Units.getUnit(unit.getUnit());
    if(u.isPresent()) {
      Log.error(String.format("0xT002 Invalid unit kind: '%s' of unit '%s'. ${tagTypeName} expects as unit kind '${dataType}'. Available '${dataType}' units are '%s'.",
          u.get().getKind(), u.get().toString(), Units.getAvailable${dataType}Units()), unit.get_SourcePositionStart());
      return false;
    }
    Log.error(String.format("0xT0003 Unit is unknown: '%s'. Available '${dataType}' units are '%s'.",
      unit.getUnit(), Units.getAvailable${dataType}Units()), unit.get_SourcePositionStart());
    return false;
  }
</#if>

  protected boolean checkValueKind(ASTValuedTag tag) {
    <#assign unitKind = "">
    <#if isUnit>
      <#assign unitKind = "UnitValue">
    <#elseif dataType = "String">
      <#assign unitKind = "StringValue">
    <#elseif dataType = "Boolean">
      <#assign unitKind = "BooleanValue">
    <#else>
      <#assign unitKind = "NumericValue">
    </#if>
    if (tag.getTagValue() != null &&
        tag.getTagValue().getValueKind().equals("${unitKind}")) {
      return true;
    }
    Log.error(String.format("0xT0004 Invalid value kind: '%s'. ${tagTypeName} expects as tag kind '${unitKind}'.",
        tag.getTagValue() != null ? tag.getTagValue().getValueKind() : "tag value is zero"),
        tag.get_SourcePositionStart());
    return false;
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