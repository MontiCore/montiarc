<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public interface ${ast.getName()}${suffixes.events()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>
  extends montiarc.rte.behavior.Behavior<${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/>>{

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  <#assign methodName = prefixes.message() + portSym.getName() + helper.portVariantSuffix(ast, portSym)>
  void ${methodName}(<@Util.getTypeString portSym.getType()/> msg);
</#list>
}