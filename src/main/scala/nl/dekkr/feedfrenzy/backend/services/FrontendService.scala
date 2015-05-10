package nl.dekkr.feedfrenzy.backend.services

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.FlowMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import java.io.IOException
import nl.dekkr.feedfrenzy.backend.model._
import nl.dekkr.feedfrenzy.backend.util.ActionExecutor

import scala.concurrent.{ExecutionContextExecutor, Future}
import spray.json.DefaultJsonProtocol

trait Protocols extends DefaultJsonProtocol {
  implicit val action = jsonFormat6(Action.apply)
  implicit val articleLinksRequest = jsonFormat2(ArticleLinksRequest.apply)
  implicit val articleLinks = jsonFormat1(ArticleLinks.apply)

  implicit val newContent = jsonFormat1(NewContent.apply)
}

trait FrontendService extends Protocols with Configuration {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: FlowMaterializer

  val logger: LoggingAdapter

  lazy val pageFetcherFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(PAGEFETCHER_INTERFACE, PAGEFETCHER_PORT)

  def pageFetchRequest(request: HttpRequest): Future[HttpResponse] =
    Source.single(request).via(pageFetcherFlow).runWith(Sink.head)

  def fetchPage(url: String): Future[Either[String, String]] = {
    pageFetchRequest(RequestBuilding.Get(s"$PAGEFETCHER_URI$url",HttpEntity(ContentTypes.`application/json`, ""))).flatMap { response =>
      response.status match {
        case OK =>  Unmarshal(response.entity).to[String].map(Right(_))
        case BadRequest => Future.successful(Left(s"$url: incorrect url"))
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"Pagefetcher request failed with status code ${response.status} and entity $entity"
          logger.error(error)
          Future.failed(new IOException(error))
        }
      }
    }
  }

  val routes = {
    logRequestResult("feedfrenzy-backend-microservice") {
      pathPrefix("v1") {
        pathPrefix("createArticleLinks") {
          (post & entity(as[ArticleLinksRequest])) { request =>
            complete {
              fetchPage(request.url).map[ToResponseMarshallable] {
                case Right(content : String) =>
                  val results = ActionExecutor.start(content,request.indexActions)
                  ArticleLinks(results)
                  //ArticleLinks(List(request.url, content))
                case Left(errorMessage) => BadRequest -> errorMessage
              }
            }
          }
        }
//        (get & path(Segment)) { ip =>
//          complete {
//            fetchIpInfo(ip).map[ToResponseMarshallable] {
//              case Right(ipInfo) => ipInfo
//              case Left(errorMessage) => BadRequest -> errorMessage
//            }
//          }
//        } ~
//        (post & entity(as[IpPairSummaryRequest])) { ipPairSummaryRequest =>
//        complete {
//          val ip1InfoFuture = fetchIpInfo(ipPairSummaryRequest.ip1)
//          val ip2InfoFuture = fetchIpInfo(ipPairSummaryRequest.ip2)
//          ip1InfoFuture.zip(ip2InfoFuture).map[ToResponseMarshallable] {
//            case (Right(info1), Right(info2)) => IpPairSummary(info1, info2)
//            case (Left(errorMessage), _) => BadRequest -> errorMessage
//            case (_, Left(errorMessage)) => BadRequest -> errorMessage
//          }
//        }
      }
    }
  }



}
