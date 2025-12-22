<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/MethodNames.ftl" as MethodNames/>
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#import "/montiarc/generator/ma2jsim/component/modes/ModeUtil.ftl" as ModeUtil>

<#assign modeAutomaton = helper.getComponentHelper().getModeAutomaton(ast).get()>
<#assign modes = helper.getModeHelper().getModes(modeAutomaton)>
<#list modes as mode>
  @Override
  public void <@MethodNames.modeInit mode.getSymbol()/>() {
    <#list helper.getModeHelper().getInstancesFromMode(mode) as sub>
      <#assign subSymbol = sub.getSymbol()>
      <#assign subCompName>this.${prefixes.subcomp()}${mode.getName()}_${subSymbol.getName()}${helper.getVariantHelper().subcomponentVariantSuffix(ast, subSymbol)}</#assign>
    </#list>
  }
</#list>
