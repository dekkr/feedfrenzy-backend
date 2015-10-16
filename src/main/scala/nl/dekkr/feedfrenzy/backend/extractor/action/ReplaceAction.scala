package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.Replace


class ReplaceAction extends BaseAction {

  def execute(vars: VariableMap, a: Replace): List[String] = {
    getVariable(a.inputVariable, vars).map(input =>
      input.replaceAllLiterally(replaceVarsInTemplate(a.find, vars), replaceVarsInTemplate(a.replaceWith, vars))
    )
  }

}
