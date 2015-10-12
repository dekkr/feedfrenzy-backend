package nl.dekkr.feedfrenzy.backend.util

import java.util.regex.{Matcher, Pattern}

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model._
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.language.implicitConversions

trait ActionExecutor {

  type VariableMap = Map[String, List[String]]

  protected final val inputVar: String = "-->Input<--"

  protected val logger = Logger(LoggerFactory.getLogger("[ActionExecutor]"))

  protected def doActions(input: VariableMap, actions: List[Action]): VariableMap =
    if (actions.nonEmpty)
      doActions(performSingleAction(actions.head, input), actions.tail)
    else
      input

  protected def map2Raw(input: String, vars: VariableMap) =
    RawVariables(vars.filter(_._1 != inputVar).map(v => RawVariable(v._1, v._2)).toList, input)

  protected def getVariable(key: Option[String], vars: VariableMap): List[String] =
    vars.getOrElse(key.getOrElse(inputVar), List.empty)


  implicit private def action2ActionType(action: Action): ActionType = {
    action.actionType.toLowerCase match {
      case "css-selector" => CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-remove" => CssSelectorRemove(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-parent" => CssSelectorParent(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "attribute" => Attribute(action.inputVariable, action.outputVariable, attribute = action.template.get)
      case "template" => Template(action.outputVariable, template = action.template.get)
      case "regex" => Regex(action.inputVariable, action.outputVariable, regex = action.template.get)
      case "date-regex" => DateRegex(action.inputVariable, action.outputVariable, regex = action.template.get)
      case "replace" => Replace(action.inputVariable, action.outputVariable, find = action.template.get, replaceWith = action.replaceWith.get)
      case "split" => Split(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case _ => throw new NoSuchMethodException
    }
  }


  private def performSingleAction(action: Action, vars: VariableMap): VariableMap = {

    val output: List[String] = action2ActionType(action) match {
      case a: Regex =>
        extractWithRegEx(getVariable(a.inputVariable, vars), a)

      case a: DateRegex =>
        extractDateWithRegEx(getVariable(a.inputVariable, vars), a)

      case a: Split =>
        getVariable(a.inputVariable, vars).flatMap(input => split(input, a.selectorPattern))

      case a: CssSelector =>
        extractWithCssSelector(getVariable(a.inputVariable, vars), a.selectorPattern, vars, includeParentHtml = false)

      case a: CssSelectorParent =>
        extractWithCssSelector(getVariable(a.inputVariable, vars), a.selectorPattern, vars, includeParentHtml = true)

      case a: CssSelectorRemove =>
        removeWithCssSelector(getVariable(a.inputVariable, vars), a.selectorPattern, vars)

      case a: Attribute =>
        extractAttribute(getVariable(a.inputVariable, vars), a.attribute)

      case a: Template =>
        List(replaceVarsInTemplate(a.template, vars))

      case a: Replace => getVariable(a.inputVariable, vars).map(input =>
          input.replaceAllLiterally(replaceVarsInTemplate(a.find, vars), replaceVarsInTemplate(a.replaceWith, vars))
        )

      case _ =>
        // TODO log error
        List("-- actionType not implemented --")
    }
    vars.filter(v => v._1.ne(action.outputVariable.getOrElse(inputVar))) + (action.outputVariable.getOrElse(inputVar) -> output)
  }


  private def split(content: String, selector: String): List[String] = {
    try {
      Jsoup.parse(content).select(selector).iterator.toList map {_.parent().html()}
    } catch {
      case e: Exception =>
        logger.debug(s"split: ${e.getMessage}")
        List.empty[String]
    }
  }


  private def removeWithCssSelector(inputList: List[String], template: String, vars:VariableMap): List[String] =
    inputList.map( input => {
        if (input != null && input.length > 0) {
          val doc = Jsoup.parse(input)
          doc.select(replaceVarsInTemplate(template, vars)).first  match {
            case removeThisContent if removeThisContent != null =>
              doc.body.html.replaceAllLiterally(removeThisContent.outerHtml(), "")
            case _ => input
          }
        } else {
          ""
        }
      }
    )


  private def extractWithCssSelector(inputList: List[String], template: String, vars: VariableMap, includeParentHtml: Boolean): List[String] = {
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


  private def extractWithRegEx(inputVar: List[String], a: Regex): List[String] =
    inputVar map { input =>
      a.regex.r.findFirstMatchIn(input) match {
        case Some(found) => found.matched
        case None => ""
      }
    }

  private def extractDateWithRegEx(inputVar: List[String], a: DateRegex): List[String] =
    inputVar map { input =>
        a.regex.r.findFirstMatchIn(input) match {
          case Some(found) =>  found.matched
          case None => ""
        }
      }



  def findGroupingNamesInRegEx(regex: String) : List[String] = {
    //TODO implement this
    """\?\<(\w+)\>""".r.findAllIn(regex).matchData.map(_.matched).toList
    List("year","day","month")
  }



  private def extractAttribute(inputList: List[String], attrib: String): List[String] = {
    inputList.map(input =>
      try {
        Jsoup.parse("<html></html").html(input).child(0).attr(attrib)
      } catch {
        case e: Exception =>
          // TODO log an error: Attribute not found or empty input
          ""
      }
    )
  }

  private def replaceVarsInTemplate(template: String, vars: VariableMap): String = {
    var output = template
    for (item <- vars) {
      output = output.replaceAllLiterally(s"{${item._1}}", if (item._2.nonEmpty) item._2.head else "")
    }
    output
  }


}