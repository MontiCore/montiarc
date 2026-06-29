<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  <#assign portAccessor>this.${prefixes.port()}${portSym.getName()}${helper.getVariantHelper().portVariantSuffix(ast, portSym)}()</#assign>
  <#assign existenceConditions = helper.getVariantHelper().getExistenceCondition(ast, portSym)/>
  <#assign methodName = prefixes.portValueOf() + portSym.getName() + helper.getVariantHelper().portVariantSuffix(ast, portSym)/>
  protected <@Util.getTypeString portSym.getType()/> ${methodName}() {
    <#if existenceConditions?has_content>
      ${tc.include("montiarc.generator.ma2jsim.component.ShadowConstants.ftl")}

      if(${javaPrinter.generateCode(existenceConditions)}) {
    </#if>
      return ${portAccessor}.isTickBlocked() ? ${helper.getTypeHelper().getNullLikeValue(portSym.getType())} : ${portAccessor}.peekBuffer().getData();

    <#if existenceConditions?has_content>
      } else throw new RuntimeException(
      "Port ${portSym.getName()} is not available in component " + getName()
      + " under the given feature configuration.");
    </#if>
  }

</#list>
