<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  <#assign portAccessor>this.${prefixes.port()}${portSym.getName()}()</#assign>
  <#assign methodName = prefixes.portValueOf() + portSym.getName()/>
  <#assign portType = helper.getComponentHelper().getPortType(ast.getSymbol(), portSym)>
  protected <@Util.getTypeString portType/> ${methodName}() {
      return ${portAccessor}.isTickBlocked() ? ${helper.getTypeHelper().getNullLikeValue(portType)} : ${portAccessor}.peekBuffer().getData();
  }

</#list>
