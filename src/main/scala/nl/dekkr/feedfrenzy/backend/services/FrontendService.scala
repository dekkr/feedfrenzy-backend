package nl.dekkr.feedfrenzy.backend.services

import java.io.IOException

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import nl.dekkr.feedfrenzy.backend.model._
import nl.dekkr.feedfrenzy.backend.util.{ArticleExtractor, ArticleLinksExtractor}
import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContextExecutor, Future}

trait Protocols extends DefaultJsonProtocol {
  implicit val action = jsonFormat6(Action.apply)
  implicit val articleLinksRequest = jsonFormat2(ArticleLinksRequest.apply)
  implicit val articleRequest = jsonFormat2(ArticleRequest.apply)
  implicit val articleLinks = jsonFormat1(ArticleLinks.apply)
  implicit val article = jsonFormat4(Article.apply)

  implicit val newContent = jsonFormat1(NewContent.apply)
}

trait FrontendService extends Protocols with Configuration {
  lazy val pageFetcherFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(PAGEFETCHER_INTERFACE, PAGEFETCHER_PORT)
  implicit val system: ActorSystem
  implicit val materializer: FlowMaterializer

  val logger: LoggingAdapter
  val routes = {
    logRequestResult("feedfrenzy-backend-microservice") {
      pathPrefix("v1") {
        pathPrefix("createArticleLinks") {
          (post & entity(as[ArticleLinksRequest])) { request =>
            complete {
              fetchPage(request.url).map[ToResponseMarshallable] {
                case Right(content : String) =>
                  new ArticleLinksExtractor().getList(content,request.actions)
                case Left(errorMessage) => BadRequest -> errorMessage
              }
            }
          }
        } ~
          pathPrefix("createArticle") {
            (post & entity(as[ArticleRequest])) { request =>
            complete {
              fetchPage(request.url).map[ToResponseMarshallable] {
                case Right(content : String) =>
                  new ArticleExtractor().getArticle(request.url,content,request.actions)
                case Left(errorMessage) => BadRequest -> errorMessage
              }
            }
          }
          }

      }
    }
  }

  implicit def executor: ExecutionContextExecutor

  def fetchPage(url: String): Future[Either[String, String]] = {
    pageFetchRequest(RequestBuilding.Get(s"$PAGEFETCHER_URI$url", HttpEntity(ContentTypes.`application/json`, ""))).flatMap { response =>
      response.status match {
        case OK => Unmarshal(response.entity).to[String].map(Right(_))
        case BadRequest => Future.successful(Left(s"$url: incorrect url"))
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"Pagefetcher request failed with status code ${response.status} and entity $entity"
          logger.error(error)
          Future.failed(new IOException(error))
        }
      }
    }
  }

  def pageFetchRequest(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(pageFetcherFlow).runWith(Sink.head)


}
