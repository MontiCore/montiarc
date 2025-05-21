<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type="arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#-- @ftlvariable name="isTop" type="boolean" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

public interface ${ast.getName()}${suffixes.comp()}<#if isTop>TOP</#if> <@Util.printTypeParameters ast/>
  extends montiarc.rte.component.Component,
          ${ast.getName()}${suffixes.output()} <@Util.printTypeParameters ast false/>,
          ${ast.getName()}${suffixes.input()} <@Util.printTypeParameters ast false/> { }