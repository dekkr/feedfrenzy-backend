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


  private def performSingleAction(action: Action, vars: VariableMap): VariableMap = {

    val output: List[String] = action.actionType match {

      case TypeOfAction.CssSelector =>
        new CssSelectorAction().execute(vars,
          CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
        )

      case TypeOfAction.CssSelectorRemove =>
        new CssRemoveAction().execute(vars,
          CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
        )

      case TypeOfAction.CssSelectorParent =>
        new CssParentSelectorAction().execute(vars,
          CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
        )

      case TypeOfAction.Attribute =>
        new AttributeAction().execute(vars,
          Attribute(action.inputVariable, action.outputVariable, attribute = action.template.get)
        )

      case TypeOfAction.Template =>
        new TemplateAction().execute(vars,
          Template(action.outputVariable, template = action.template.get)
        )

      case TypeOfAction.Regex =>
        new RegexAction().execute(vars,
          Regex(action.inputVariable, action.outputVariable, regex = action.template.get)
        )

      case TypeOfAction.DateParser =>
        new ParseDateAction().execute(vars,
          DateParser(action.inputVariable, action.outputVariable, pattern = action.pattern.get, locale = action.locale.get, padTime = action.padTime.getOrElse(false))
        )

      case TypeOfAction.Replace =>
        new ReplaceAction().execute(vars,
          Replace(action.inputVariable, action.outputVariable, find = action.template.get, replaceWith = action.replaceWith.get)
        )

      case TypeOfAction.Split =>
        new SplitAction().execute(vars,
          Split(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
        )

      case _ =>
        throw new NoSuchMethodException
    }
    vars.filter(v => v._1.ne(action.outputVariable.getOrElse(inputVar))) + (action.outputVariable.getOrElse(inputVar) -> output)
  }


}