package nl.dekkr.feedfrenzy.backend.util

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model.{Action, Article, RawVariables}
import org.slf4j.LoggerFactory


class ArticleExtractor extends ActionExecutor {

  override protected val logger = Logger(LoggerFactory.getLogger("[ArticleExtractor]"))

  def getArticle(url: String, input: String, actions: List[Action]): Article = {
    val vars = getVars(url, input, actions)
    Article(
      uid = getVariable(Some("uid"), vars).headOption.getOrElse(""),
      title = getVariable(Some("title"), vars).headOption.getOrElse(""),
      author = getVariable(Some("author"), vars).headOption,
      content = getVariable(Some("content"), vars).headOption.getOrElse(""),
      tags = getVariable(Some("tags"), vars)
    )
  }

  def getRaw(url: String, input: String, actions: List[Action]): RawVariables =
    map2Raw(input, getVars(url, input, actions))

  private def getVars(url: String, input: String, actions: List[Action]) =
    doActions(args2map(url, input), actions.sortBy(a => a.order))

  private def args2map(url: String, input: String): Map[String, List[String]] =
    Map(inputVar -> List(input), "uid" -> List(url))

}
