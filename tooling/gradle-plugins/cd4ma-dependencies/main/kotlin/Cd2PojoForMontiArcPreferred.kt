/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.cd2pojo.CD2POJO_API_SYMBOL_USAGE
import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.MultipleCandidatesDetails
import org.gradle.api.attributes.Usage

/**
 * Using this rule, dependency variants with perfectly matching [Usage] attribute value [CD2POJO_4_MONTIARC_USAGE] are
 * preferred over [CD2POJO_API_SYMBOL_USAGE] when we search for [Usage] values of [CD2POJO_4_MONTIARC_USAGE].
 */
class Cd2PojoForMontiArcPreferred : AttributeDisambiguationRule<Usage> {
  override fun execute(t: MultipleCandidatesDetails<Usage>): Unit = with (t) {
    if (consumerValue != null && consumerValue!!.name == CD2POJO_4_MONTIARC_USAGE) {

      val candidates = candidateValues.filterNotNull()
      val equalMatch = candidates.firstOrNull { it.name == CD2POJO_4_MONTIARC_USAGE }
      val fromOrigLang = candidates.firstOrNull { it.name == CD2POJO_API_SYMBOL_USAGE }

      if (equalMatch != null) {
        closestMatch(equalMatch)
      } else if (fromOrigLang != null) {
        closestMatch(fromOrigLang)
      }
    }
  }
}
