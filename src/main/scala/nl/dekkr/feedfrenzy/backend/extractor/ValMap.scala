package nl.dekkr.feedfrenzy.backend.extractor


trait ValMap {

  type VariableMap = Map[String, List[String]]

  protected final val inputVar: String = "-->Input<--"


  protected def replaceVarsInTemplate(template: String, vars: VariableMap): String = {
    var output = template
    for (item <- vars) {
      output = output.replaceAllLiterally(s"{${item._1}}", if (item._2.nonEmpty) item._2.head else "")
    }
    output
  }

  protected def getVariable(key: Option[String], vars: VariableMap): List[String] =
    vars.getOrElse(key.getOrElse(inputVar), List.empty)

}
