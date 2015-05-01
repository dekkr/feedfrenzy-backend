package nl.dekkr.feedfrenzy.backend.services

import nl.dekkr.feedfrenzy.backend.model._

import scala.util.{Failure, Success, Try}
import scalaj.http.Http

class BackendService(pagefetcher_uri: String) extends BackendSystem {


  private val USER_AGENT: String = "feedfrenzy-backend"
  private val CHARSET: String = "UTF-8"

  def getContent(request: PageUrl): BackendResult = {
      Try(this.pageContent(request.url).charset(CHARSET)) match {
        case Success(content) =>
          processRequest(request, content)
        case Failure(e) => Error(s"${e.getMessage}")
      }
  }

  private def processRequest(request: PageUrl, content: Http.Request): BackendResult = {
      NewContent(content.asString)
  }

  private def pageContent(uri: String) = Http(pagefetcher_uri + uri).header("User-Agent", USER_AGENT)

}
