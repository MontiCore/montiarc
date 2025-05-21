<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->

<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign CLASS>${ast.getName()}${suffixes.syncMsg()}<#if isTop>${suffixes.top()}</#if></#assign>

public <#if isTop>abstract</#if> class ${CLASS} <@Util.printTypeParameters ast/> {

  <#list ast.getSymbol().getAllIncomingPorts() as port>
    public final <@Util.getTypeString port.getType()/> ${port.getName()}${helper.portVariantSuffix(ast, port)};
  </#list>

  public ${CLASS}(
    <#list ast.getSymbol().getAllIncomingPorts() as port>
      <@Util.getTypeString port.getType()/> ${port.getName()}${helper.portVariantSuffix(ast, port)}
      <#sep>, </#sep>
    </#list>

  ) {
  <#list ast.getSymbol().getAllIncomingPorts() as port>
    <#assign portFieldName = port.getName() + helper.portVariantSuffix(ast, port)>
    this.${portFieldName} = ${portFieldName};
  </#list>
  }
}