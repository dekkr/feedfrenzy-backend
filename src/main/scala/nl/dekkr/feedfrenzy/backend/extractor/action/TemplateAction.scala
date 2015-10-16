package nl.dekkr.feedfrenzy.backend.extractor.action

import nl.dekkr.feedfrenzy.backend.model.Template

class TemplateAction extends BaseAction {

  def execute(vars: VariableMap, a : Template): List[String] =
    List(replaceVarsInTemplate(a.template, vars))

  
}
