<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public <#if isTop>abstract</#if> class
  ${ast.getName()}${suffixes.component()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>