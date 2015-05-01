package nl.dekkr.feedfrenzy.backend

import akka.http.Http
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import nl.dekkr.feedfrenzy.backend.actors.{BootedCore, CoreActors}
import nl.dekkr.feedfrenzy.backend.services.{BackendService, FrontendService}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps


object Boot extends App with BootedCore with CoreActors with FrontendService {


  private val logger = Logger(LoggerFactory.getLogger("[Feedfrenzy-backend]"))
  val config = ConfigFactory.load()
  val CONFIG_BASE = "nl.dekkr.feedfrenzy.backend"


  private val PAGEFETCHER_URI = config.getString(s"$CONFIG_BASE.pagefetcher-uri")

  implicit val backend = new BackendService(PAGEFETCHER_URI)

  startApi()

  def startApi(): Unit = {
    implicit val materializer = ActorFlowMaterializer()
    val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
      Http(system).bind(
        interface = config.getString(s"$CONFIG_BASE.api.interface"),
        port = config.getInt(s"$CONFIG_BASE.api.port"))

    //val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      logger.debug("Accepted new connection from " + connection.remoteAddress)
      connection handleWithSyncHandler requestHandler
    }).run()
  }

}
