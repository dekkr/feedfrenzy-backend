package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.Regex

class RegexAction extends BaseAction {


  def execute(vars: VariableMap, a: Regex): List[String] =
    getVariable(a.inputVariable, vars) map { input =>
      a.regex.r.findFirstMatchIn(input) match {
        case Some(found) => found.matched
        case None => ""
      }
    }

}
