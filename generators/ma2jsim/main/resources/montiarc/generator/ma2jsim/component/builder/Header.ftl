<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public <#if isTop>abstract</#if> class
  ${ast.getName()}${suffixes.comp()}${suffixes.builder()}<#if isTop>${suffixes.top()}</#if> <@Util.printTypeParameters ast/>