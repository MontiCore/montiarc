<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected <@Util.getStaticPortClass portSym ast.getSymbol().isAtomic()/><<@Util.getTypeString portSym.getType() true/>> ${prefixes.port()}${portSym.getName()} = null;