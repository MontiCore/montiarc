<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentInstance ast -->
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
protected <@Util.getCompTypeString ast.getSymbol().getType() suffixes.component()/> ${prefixes.subcomp()}${ast.getName()}() {
    return this.${prefixes.subcomp()}${ast.getName()};
}