package nl.dekkr.feedfrenzy.backend.util

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model.{Action, ArticleLinks, RawVariables}
import org.slf4j.LoggerFactory


class ArticleLinksExtractor extends ActionExecutor {


  override protected val logger = Logger(LoggerFactory.getLogger("[ArticleLinksExtractor]"))

  def getList(input: String, actions: List[Action]): ArticleLinks =
    ArticleLinks(getVariable(Some(inputVar), getVars(input, actions)))

  def getRaw(input: String, actions: List[Action]): RawVariables =
    map2Raw(input, getVars(input, actions))


  private def getVars(input: String, actions: List[Action]) =
    doActions(args2map(input), actions.sortBy(a => a.order))

  private def args2map(input: String): Map[String, List[String]] =
    Map(inputVar -> List(input))


}
