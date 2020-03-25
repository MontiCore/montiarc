/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.SymbolTableCreator;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import montiarc._symboltable.MontiArcLanguage;

import java.util.Optional;

/**
 * TODO
 *
 */
public class DynamicMontiArcLanguage extends DynamicMontiArcLanguageTOP {
    public static final String FILE_ENDING = "arc";

    /**
     * Constructor for extension._symboltable.ComponentPropertiesLanguage
     */
    public DynamicMontiArcLanguage() {
        super("DynamicMontiArcLanguage", FILE_ENDING);
    }

    /**
     * @see de.monticore.ModelingLanguage#getSymbolTableCreator(de.monticore.symboltable.ResolvingConfiguration,
     * de.monticore.symboltable.MutableScope)
     */
    @Override
    public Optional<DynamicMontiArcSymbolTableCreator> getSymbolTableCreator(
            ResolvingConfiguration resolvingConfiguration,
            MutableScope enclosingScope) {

        final DynamicMontiArcSymbolTableCreator symTabCreator =
                new DynamicMontiArcSymbolTableCreator(resolvingConfiguration, enclosingScope);
        return Optional.of(symTabCreator);
    }

    /**
     * @see de.monticore.CommonModelingLanguage#provideModelLoader()
     */
    @Override
    protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
        return new DynamicMontiArcModelLoader(this);
    }

    @Override
    protected void initResolvingFilters() {

        super.initResolvingFilters();
        MontiArcLanguage montiarc = new MontiArcLanguage();
        addResolvingFilters(montiarc.getResolvingFilters());
        //addResolvingFilter(new CommonResolvingFilter<DynamicConnectorSymbol>(DynamicConnectorSymbol.KIND));
        //addResolvingFilter(new CommonResolvingFilter<ModeDeclarationSymbol>(ModeDeclarationSymbol.KIND));
    }

}
