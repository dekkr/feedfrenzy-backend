package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.CssSelectorRemove
import org.jsoup.Jsoup

class RemoveWithCssSelectorAction extends BaseAction {

  def execute(vars: VariableMap, a:CssSelectorRemove): List[String] =
    getVariable(a.inputVariable, vars).map(input => {
      if (input != null && input.length > 0) {
        val doc = Jsoup.parse(input)
        doc.select(replaceVarsInTemplate(a.selectorPattern, vars)).first match {
          case removeThisContent if removeThisContent != null =>
            doc.body.html.replaceAllLiterally(removeThisContent.outerHtml(), "")
          case _ => input
        }
      } else {
        ""
      }
    }
    )

}
