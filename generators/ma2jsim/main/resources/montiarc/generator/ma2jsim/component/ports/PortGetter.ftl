<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="ast" type=" arcbasis._ast.ASTArcComponentType" -->
<#-- @ftlvariable name="helper" type="montiarc.generator.util.Helper" -->
${tc.signature("portSym")}
<#import "/montiarc/generator/ma2jsim/util/Util.ftl" as Util>
<#assign variants = helper.getVariantHelper().getVariants(ast)>
<#assign hasOnlyOneVariant = variants?size == 1>

@Override
public <@Util.getStaticPortInterface portSym/><<@Util.getPortTypeString portSym.getType()/>>
  ${prefixes.port()}${portSym.getName()}${helper.getVariantHelper().portVariantSuffix(ast, portSym)}() {

    <#if hasOnlyOneVariant>
      return this.${prefixes.port()}${portSym.getName()};
    <#else>
      if (java.util.Set.of(
        <#list helper.getVariantHelper().getVariantsWithPort(ast, portSym) as v> ${helper.getVariantHelper().variantSuffix(v)} <sep>, </#list>
        ).contains(this.variantID)) {
        return this.${prefixes.port()}${portSym.getName()}${helper.getVariantHelper().portVariantSuffix(ast, portSym)};
      } else {
        assert false : "Component ${ast.getName()} is not correctly configured, no variant selected";
        return null;
      }
    </#if>
}