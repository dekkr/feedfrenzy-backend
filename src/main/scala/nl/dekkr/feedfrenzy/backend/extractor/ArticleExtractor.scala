package nl.dekkr.feedfrenzy.backend.extractor

import java.time.OffsetDateTime
import java.time.format.{DateTimeFormatter, DateTimeParseException}

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
      tags = getVariable(Some("tags"), vars),
      createdDate = convertToDate(getVariable(Some("createdDate"), vars).headOption),
      updatedDate = convertToDate(getVariable(Some("updatedDate"), vars).headOption)
    )
  }

  private def convertToDate(dateStr: Option[String]) = dateStr match {
    case Some(date) =>
      try {
        Some(OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME))
      } catch {
        case e: DateTimeParseException =>
          Some(OffsetDateTime.now())
      }
    case None => None
  }

  private def getVars(url: String, input: String, actions: List[Action]) =
    doActions(args2map(url, input), actions.sortBy(a => a.order))

  private def args2map(url: String, input: String): Map[String, List[String]] =
    Map(inputVar -> List(input), "uid" -> List(url))

  def getRaw(url: String, input: String, actions: List[Action]): RawVariables =
    map2Raw(input, getVars(url, input, actions))

}
