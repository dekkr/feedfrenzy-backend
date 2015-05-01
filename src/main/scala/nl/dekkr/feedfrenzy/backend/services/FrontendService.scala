package nl.dekkr.feedfrenzy.backend.services

import akka.http.model.HttpMethods._
import akka.http.model.StatusCodes._
import akka.http.model._
import nl.dekkr.feedfrenzy.backend.model._

import scala.language.implicitConversions

trait FrontendService {
  implicit val backend: BackendSystem

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(
        entity = HttpEntity(MediaTypes.`text/html`,
          <html>
            <head>
              <title>Feedfrenzy-backend by DekkR projects</title>
            </head>
            <body>
              <h1>Feedfrenzy-backend</h1>
              <p>Ready to serve.</p>
            </body>
          </html>.buildString(stripComments = true)
        ))

    case HttpRequest(GET, uri, _, _, _) if uri.path.startsWith(Uri.Path("/v1/articles")) =>
      uri.query.get("url") match {
        case Some(url) =>
          try {
            backend.getArticles(uri) match {
              case NewContent(content) => HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, content))
              case Error(msg) => HttpResponse(BadRequest, entity = s"$msg")
              case _ => HttpResponse(BadRequest, entity = s"Bad request")
            }
          }
          catch {
            case e: Exception => HttpResponse(BadRequest, entity = s"${e.getMessage}")
          }
        case None =>
          HttpResponse(NotFound, entity = "Missing url!")
      }

    case HttpRequest(GET, uri, _, _, _) if uri.path.startsWith(Uri.Path("/v1/article")) =>
      uri.query.get("url") match {
        case Some(url) =>
          try {
            backend.getArticle(uri) match {
              case NewContent(content) => HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, content))
              case Error(msg) => HttpResponse(BadRequest, entity = s"$msg")
              case _ => HttpResponse(BadRequest, entity = s"Bad request")
            }
          }
          catch {
            case e: Exception => HttpResponse(BadRequest, entity = s"${e.getMessage}")
          }
        case None =>
          HttpResponse(NotFound, entity = "Missing url!")
      }

    case _: HttpRequest =>
      HttpResponse(NotFound, entity = "Unknown resource!")
  }

  implicit def uri2PageUri(uri: Uri): PageUrl = {
    val maxAge: Option[Int] = uri.query.get("maxAge") match {
      case Some(value: String) => Some(value.toInt)
      case None => Some(1440)
    }
    val raw: Option[Boolean] = uri.query.get("raw") match {
      case Some(value: String) => Some(value.toBoolean)
      case None => Some(false)
    }
    PageUrl(url = uri.query.get("url").getOrElse(""), maxAge = maxAge, raw = raw)
  }

}