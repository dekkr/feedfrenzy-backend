package nl.dekkr.feedfrenzy.backend

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorFlowMaterializer

import nl.dekkr.feedfrenzy.backend.actors.{BootedCore, CoreActors}
import nl.dekkr.feedfrenzy.backend.services.FrontendService

import scala.language.postfixOps


object Boot extends App with BootedCore with CoreActors with FrontendService {

  override val logger = Logging(system, getClass)

  override implicit lazy val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorFlowMaterializer()

  Http().bindAndHandle(routes,interface = API_INTERFACE, port = API_PORT
  )

}
