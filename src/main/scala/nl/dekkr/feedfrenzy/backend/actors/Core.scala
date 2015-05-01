package nl.dekkr.feedfrenzy.backend.actors

import akka.actor.ActorSystem

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait Core {

  implicit def system: ActorSystem

}
