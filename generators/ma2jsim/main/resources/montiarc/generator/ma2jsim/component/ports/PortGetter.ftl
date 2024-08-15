<#-- (c) https://github.com/MontiCore/monticore -->
<#-- ASTComponentType ast -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign variants = helper.getVariants(ast)>
<#assign hasOnlyOneVariant = variants?size == 1>

public <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>>
  ${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)}() {

    <#if hasOnlyOneVariant>
      return this.${prefixes.port()}${portSym.getName()};
    <#else>
      if (java.util.Set.of(
        <#list helper.getVariantsWithPort(ast, portSym) as v> ${helper.variantSuffix(v)} <sep>, </#list>
        ).contains(this.variantID)) {
        return this.${prefixes.port()}${portSym.getName()}${helper.portVariantSuffix(ast, portSym)};
      } else {
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return null;
      }
    </#if>
}