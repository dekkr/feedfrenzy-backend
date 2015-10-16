package nl.dekkr.feedfrenzy.backend.extractor

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.extractor.action._
import nl.dekkr.feedfrenzy.backend.model._
import org.slf4j.LoggerFactory

import scala.language.implicitConversions

trait ActionExecutor extends ValMap {

  protected val logger = Logger(LoggerFactory.getLogger("[ActionExecutor]"))

  protected def doActions(input: VariableMap, actions: List[Action]): VariableMap =
    if (actions.isEmpty)
      input
    else
      doActions(performSingleAction(actions.head, input), actions.tail)


  protected def map2Raw(input: String, vars: VariableMap) =
    RawVariables(vars.filter(_._1 != inputVar).map(v => RawVariable(v._1, v._2)).toList, input)


  private def action2ActionType(action: Action): ActionType = {
    action.actionType.toLowerCase match {
      case "css-selector" => CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-remove" => CssSelectorRemove(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-parent" => CssSelectorParent(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "attribute" => Attribute(action.inputVariable, action.outputVariable, attribute = action.template.get)
      case "template" => Template(action.outputVariable, template = action.template.get)
      case "regex" => Regex(action.inputVariable, action.outputVariable, regex = action.template.get)
      case "date-parser" => DateParser(action.inputVariable, action.outputVariable, pattern = action.pattern.get, locale = action.locale.get, padTime = action.padTime.getOrElse(false))
      case "replace" => Replace(action.inputVariable, action.outputVariable, find = action.template.get, replaceWith = action.replaceWith.get)
      case "split" => Split(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case _ => throw new NoSuchMethodException
    }
  }

  private def performSingleAction(action: Action, vars: VariableMap): VariableMap = {

    val output: List[String] = action2ActionType(action) match {

      case a: Regex => new RegexAction().execute(vars, a)

      case a: DateParser => new ParseDate().execute(vars, a)

      case a: Split => new SplitAction().execute(vars, a)

      case a: CssSelector => new CssSelectorAction().execute(vars, a)

      case a: CssSelectorParent => new CssParentSelectorAction().execute(vars, CssSelector(inputVariable = a.inputVariable,outputVariable = a.outputVariable,selectorPattern = a.selectorPattern))

      case a: CssSelectorRemove => new RemoveWithCssSelectorAction().execute(vars, a)

      case a: Attribute => new AttributeAction().execute(vars, a)

      case a: Template => new TemplateAction().execute(vars,a)

      case a: Replace => new ReplaceAction().execute(vars,a)

      case _ =>
        // TODO log error
        List("-- actionType not implemented --")
    }
    vars.filter(v => v._1.ne(action.outputVariable.getOrElse(inputVar))) + (action.outputVariable.getOrElse(inputVar) -> output)
  }



}