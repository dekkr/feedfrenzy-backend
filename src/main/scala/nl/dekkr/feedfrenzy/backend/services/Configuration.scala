package nl.dekkr.feedfrenzy.backend.services

import com.typesafe.config.ConfigFactory

/**
 * Author: matthijs 
 * Created on: 02 May 2015.
 */
trait Configuration {
  val config = ConfigFactory.load()

  private val CONFIG_BASE = "nl.dekkr.feedfrenzy.backend"

  val API_INTERFACE = config.getString(s"$CONFIG_BASE.api.interface")
  val API_PORT = config.getInt(s"$CONFIG_BASE.api.port")

  val PAGEFETCHER_INTERFACE = config.getString(s"$CONFIG_BASE.pagefetcher.interface")
  val PAGEFETCHER_PORT = config.getInt(s"$CONFIG_BASE.pagefetcher.port")
  val PAGEFETCHER_URI = config.getString(s"$CONFIG_BASE.pagefetcher.uri")
}
