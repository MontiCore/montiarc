<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()>
<#assign compName>${ast.getName() + suffixes.compImpl()}<#if isTop>${suffixes.top()}</#if></#assign>
<#assign syncMsgClass>${ast.getName()}${suffixes.syncMsg()}<@Util.printTypeParameters ast false/></#assign>
<#assign eventClass>${ast.getName()}${suffixes.events()}<@Util.printTypeParameters ast false/></#assign>

public <#if isTop> abstract </#if> class ${compName}<@Util.printTypeParameters ast/>
  extends <#if hasModeAutomaton> montiarc.rte.component.AbstractModeComponent<${syncMsgClass}, ${eventClass}, ${ast.getName()}${suffixes.modeAutomaton()}>
          <#else> montiarc.rte.component.AbstractComponent<${syncMsgClass}, ${eventClass}> </#if>
  implements
    ${ast.getName()}${suffixes.comp()}<@Util.printTypeParameters ast false/>,
    ${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/>
    <#if hasModeAutomaton>, ${ast.getName()}${suffixes.contextForModes()}<@Util.printTypeParameters ast false/></#if>