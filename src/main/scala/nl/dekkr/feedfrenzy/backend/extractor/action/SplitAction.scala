package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model._
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.language.implicitConversions


class SplitAction extends BaseAction {

  def execute(vars: VariableMap, a: Split): List[String] = {
    getVariable(a.inputVariable, vars).flatMap(input => split(input, a.selectorPattern))
  }


  private def split(content: String, selector: String): List[String] = {
    try {
      Jsoup.parse(content).select(selector).iterator.toList map {
        _.parent().html()
      }
    } catch {
      case e: Exception =>
        logger.debug(s"split: ${e.getMessage}")
        List.empty[String]
    }
  }

}
