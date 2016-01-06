package nl.dekkr.feedfrenzy.backend.test

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scalaj.http.HttpOptions


class MockPageFetcher {

  implicit lazy val system = ActorSystem("mock-page-fetch-service")

  val CONFIG_BASE = "nl.dekkr.feedfrenzy.backend.pagefetcher"

  private val USER_AGENT: String = "Mozilla/5.0)"

  private val logger = Logger(LoggerFactory.getLogger("MockPageFetcher"))
  val config = ConfigFactory.load()


  val dummyResponse = akka.http.scaladsl.model.HttpResponse(
    entity = HttpEntity(MediaTypes.`text/html`,
      """<html>
            <body>
              <h1>Mock Page Fetcher V1</h1>
            </body>
          </html>""".stripMargin
    ))

  val requestHandler: akka.http.scaladsl.model.HttpRequest => akka.http.scaladsl.model.HttpResponse = {
    case HttpRequest(HttpMethods.GET, uri, _, _, _) if uri.path.startsWith(Uri.Path("/v1/page")) => {
      uri.query.get("url") match {
        case Some(url: String) =>
          Try(collectPageContent(url, USER_AGENT)) match {
            case Success(content) =>
              akka.http.scaladsl.model.HttpResponse(entity = HttpEntity(MediaTypes.`text/html`, content.asString.body))
            case Failure(e) => dummyResponse
          }
        case None => dummyResponse
      }
    }
    case _: akka.http.scaladsl.model.HttpRequest =>
      akka.http.scaladsl.model.HttpResponse(NotFound, entity = "Unknown resource")
  }


  private def collectPageContent(uri: String, userAgent: String) = scalaj.http.Http(uri).option(HttpOptions.followRedirects(shouldFollow = true)).header("User-Agent", userAgent)

  def startApi(): Unit = {
    logger.info(s"Enabling Mock pagefetcher...")
    implicit val materializer = ActorMaterializer()
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http(system).bind(
        interface = config.getString(s"$CONFIG_BASE.interface"),
        port = config.getInt(s"$CONFIG_BASE.port"))

    serverSource.to(Sink.foreach { connection =>
      logger.debug("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler requestHandler
    }).run()
  }


}
