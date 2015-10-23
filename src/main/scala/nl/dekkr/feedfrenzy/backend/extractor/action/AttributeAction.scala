package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.Attribute
import org.jsoup.Jsoup


class AttributeAction extends BaseAction {

  def execute(vars: VariableMap, a:Attribute): List[String] = {
    getVariable(a.inputVariable, vars).map(input =>
      try {
        Jsoup.parse("<html></html").html(input).child(0).attr(a.attribute)
      } catch {
        case e: Exception =>
          logger.debug(s"Attribute [${a.attribute}] not found or empty input")
          ""
      }
    )
  }

}
