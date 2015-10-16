package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.CssSelector
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.language.implicitConversions

class CssSelectorAction extends BaseAction {

  protected val includeParentHtml = false

  def execute(vars: VariableMap, a: CssSelector): List[String] = {
    val inputList = getVariable(a.inputVariable, vars)
    val template = a.selectorPattern
    replaceVarsInTemplate(template, vars) match {
      case updatedTemplate: String if updatedTemplate.nonEmpty =>
        inputList.flatMap(input => extractCss(input, updatedTemplate, includeParentHtml))
      case updatedTemplate: String =>
        logger.debug(s"Empty string - $template")
        List.empty[String]
    }
  }

  private def extractCss(input: String, updatedTemplate: String, includeParentHtml: Boolean): List[String] = {
    if (input != null && input.length > 0) {
      Jsoup.parse(input).select(updatedTemplate) match {
        case contentList if contentList != null =>
          if (includeParentHtml) {
            contentList.map(_.outerHtml()).toList
          } else {
            contentList.map(_.html()).toList
          }
        case _ => List.empty[String]
      }
    } else {
      List.empty[String]
    }

  }

}
