package nl.dekkr.feedfrenzy.backend.util

import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.model.{Action, Article}
import org.slf4j.LoggerFactory


class ArticleExtractor extends ActionExecutor {

  override protected val logger = Logger(LoggerFactory.getLogger("[ArticleExtractor]"))

  def getArticle(url: String, input: String, actions: List[Action]): Article = {
    val vars = doActions(Map(inputVar -> List(input), "uid" -> List(url)), actions.sortBy(a => a.order))
    Article(
      uid = getVariable(Some("uid"), vars).headOption.getOrElse(""),
      title = getVariable(Some("title"), vars).headOption.getOrElse(""),
      author = getVariable(Some("author"), vars).headOption,
      content = getVariable(Some("content"), vars).headOption.getOrElse("")
    )
  }

  //TODO add raw variant, returning the map of all vars


}
