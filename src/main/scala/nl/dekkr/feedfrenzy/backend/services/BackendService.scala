package nl.dekkr.feedfrenzy.backend.services

import nl.dekkr.feedfrenzy.backend.model._

import scala.util.{Failure, Success, Try}
import scalaj.http.{HttpRequest, Http}

class BackendService(pagefetcher_uri: String) extends BackendSystem {


  private val USER_AGENT: String = "feedfrenzy-backend"
  private val CHARSET: String = "UTF-8"

  def getArticles(request: PageUrl): BackendResult = {
      Try(this.pageContent(request.url).charset(CHARSET)) match {
        case Success(content) =>
          processRequest(request, content)
        case Failure(e) => Error(s"${e.getMessage}")
      }
  }

  def getArticle(request: PageUrl): BackendResult = {
    Try(this.pageContent(request.url).charset(CHARSET)) match {
      case Success(content) =>
        processRequest(request, content)
      case Failure(e) => Error(s"${e.getMessage}")
    }
  }


  private def processRequest(request: PageUrl, content: HttpRequest): BackendResult = {
      NewContent(content.asString.body)
  }

  private def pageContent(uri: String) = Http(pagefetcher_uri + uri).header("User-Agent", USER_AGENT)

}
