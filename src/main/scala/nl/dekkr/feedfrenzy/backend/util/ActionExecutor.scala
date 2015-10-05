package nl.dekkr.feedfrenzy.backend.util

import java.util.regex.{Matcher, Pattern}

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model._
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.language.implicitConversions

trait ActionExecutor {

  protected final val inputVar: String = "-->Input"

  protected val logger = Logger(LoggerFactory.getLogger("[ActionExecutor]"))

  protected def doActions(input: Map[String, List[String]], actions: List[Action]): Map[String, List[String]] = {
    if (actions.isEmpty)
      input
    else
      doActions(performSingleAction(actions.head, input), actions.tail)
  }

  protected def getVariable(key: Option[String], vars: Map[String, List[String]]): List[String] =
    vars.getOrElse(key.getOrElse(inputVar), List.empty)


  implicit private def action2ActionType(action: Action): ActionType = {
    action.actionType.toLowerCase match {
      case "css-selector" => CssSelector(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-remove" => CssSelectorRemove(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "css-parent" => CssSelectorParent(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case "attribute" => Attribute(action.inputVariable, action.outputVariable, attribute = action.template.get)
      case "template" => Template(action.outputVariable, template = action.template.get)
      case "regex" => Regex(action.inputVariable, action.outputVariable, regex = action.template.get)
      case "replace" => Replace(action.inputVariable, action.outputVariable, find = action.template.get, replaceWith = action.replaceWith.get)
      case "split" => Split(action.inputVariable, action.outputVariable, selectorPattern = action.template.get)
      case _ => throw new NoSuchMethodException
    }
  }


  private def performSingleAction(action: Action, vars: Map[String, List[String]]): Map[String, List[String]] = {

    val output: List[String] = action2ActionType(action) match {
      case a: Regex =>
        extractWithRegEx(getVariable(a.inputVariable, vars), a)

      case a: Split =>
        val inputList = getVariable(a.inputVariable, vars)
        val input = if (inputList.nonEmpty) inputList.head else ""
        split(input, a.selectorPattern)

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
      //println(s"TEMPLATE [$template] [$output]")

      case a: Replace =>
        for {
          input <- getVariable(a.inputVariable, vars)
          updated = input.replaceAllLiterally(replaceVarsInTemplate(a.find, vars), replaceVarsInTemplate(a.replaceWith, vars))
        } yield updated


      case _ =>
        // TODO log error
        List("-- actionType not implemented --")
    }
    logger.debug("################### output #######################")
    logger.debug(s"$output")
    logger.debug("******************* vars *************************")
    logger.debug(s"$vars")

    vars.filter(v => v._1.ne(action.outputVariable.getOrElse(inputVar))) + (action.outputVariable.getOrElse(inputVar) -> output)
  }


  private def split(content: String, selector: String): List[String] = {
    try {
      val blockList = Jsoup.parse(content).select(selector).iterator.toList
      blockList map {
        _.parent().html()
      }
    } catch {
      case e: Exception =>
        logger.debug(s"split: ${e.getMessage}")
        List.empty[String]
    }
  }


  private def removeWithCssSelector(inputList: List[String], template: String, vars: Map[String, List[String]]): List[String] = {
    for {
      input <- inputList
      output = {
        if (input != null && input.length > 0) {
          val doc = Jsoup.parse(input)
          val removeThisContent = doc.select(replaceVarsInTemplate(template, vars)).first
          if (removeThisContent != null) {
            doc.body.html.replaceAllLiterally(removeThisContent.outerHtml(), "")
          } else {
            input
          }
        } else {
          ""
        }
      }
    } yield output
  }


  private def extractWithCssSelector(inputList: List[String], template: String, vars: Map[String, List[String]], includeParentHtml: Boolean): List[String] = {
    for {
      input <- inputList
      output = {
        if (input != null && input.length > 0) {
          val doc = Jsoup.parse(input)
          //val doc = Jsoup.parse("<html></html").html(input)
          val updatedTemplate: String = replaceVarsInTemplate(template, vars)
          if (updatedTemplate.isEmpty) {
            logger.debug(s"Empty string - $template")
            ""
          } else {
            val contentList = doc.select(updatedTemplate)
            if (contentList != null) {
              if (includeParentHtml) {
                contentList.outerHtml()
              } else {
                contentList.html()
              }
            } else {
              ""
            }
          }
        } else {
          ""
        }
      }
    } yield output
  }

  private def extractWithRegEx(inputVar: List[String], a: Regex): List[String] = {
    try {
      val p: Pattern = Pattern.compile(a.regex)
      for {
        input <- inputVar
        m: Matcher = p.matcher(input)
        result = if (m.find) {
          m.group(1)
        } else {
          ""
        }
      } yield result
    }
    catch {
      case e: Exception =>
        // TODO log exception
        List.empty
    }
  }


  private def extractAttribute(inputList: List[String], attrib: String): List[String] = {
    for {
      input <- inputList
      output = {
        try {
          Jsoup.parse("<html></html").html(input).child(0).attr(attrib)
        } catch {
          case e: Exception =>
            // TODO log an error: Attribute not found or empty input
            ""
        }

      }
    } yield output
  }

  private def applyTemplate(key: String, values: List[String], template: String): List[String] = {
    for {
      value <- values
      output = template.replaceAllLiterally(s"{$key}", value)
    } yield output
  }


  private def replaceVarsInTemplate(template: String, vars: Map[String, List[String]]): String = {
    var output = template
    for (item <- vars) {
      //println(s"$output ::: ${item._1} :::  ${item._2}")
      output = output.replaceAllLiterally(s"{${item._1}}", if (item._2.size > 0) item._2.head else "")
    }
    output
  }


}