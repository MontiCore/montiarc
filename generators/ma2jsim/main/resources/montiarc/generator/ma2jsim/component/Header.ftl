<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("isTop")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>

<#assign hasModeAutomaton = helper.getModeAutomaton(ast).isPresent()>

public <#if isTop>abstract</#if> class ${ast.getName()}${suffixes.component()}<#if isTop>${suffixes.top()}</#if><@Util.printTypeParameters ast/>  implements
  ${ast.getName()}${suffixes.context()}<@Util.printTypeParameters ast false/>,
  <#if hasModeAutomaton>${ast.getName()}${suffixes.contextForModes()}<@Util.printTypeParameters ast false/>,</#if>
  montiarc.rte.component.ITimedComponent