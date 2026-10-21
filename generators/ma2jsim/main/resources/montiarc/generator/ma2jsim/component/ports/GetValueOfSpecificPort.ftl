<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#list ast.getSymbol().getAllIncomingPorts() as portSym>
  <#assign portAccessor>this.${prefixes.port()}${portSym.getName()}()</#assign>
  <#assign methodName = prefixes.portValueOf() + portSym.getName()/>
  protected <@Util.getTypeString portSym.getType()/> ${methodName}() {
      return ${portAccessor}.isTickBlocked() ? ${helper.getTypeHelper().getNullLikeValue(portSym.getType())} : ${portAccessor}.peekBuffer().getData();
  }

</#list>
