<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign portType = helper.getComponentHelper().getPortType(ast.getSymbol(), portSym)>
protected montiarc.rte.port.InOutPort<<@Util.getPortTypeString portType/>, <@Util.getPortTypeString portType/>> ${prefixes.port()}${portSym.getName()} = null;
