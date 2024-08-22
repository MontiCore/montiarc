<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected montiarc.rte.port.InOutPort<<@Util.getPortTypeString portSym.getType()/>> ${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)} = null;