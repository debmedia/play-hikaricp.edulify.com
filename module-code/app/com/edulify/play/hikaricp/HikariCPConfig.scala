/*
 * Copyright 2014 Edulify.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.edulify.play.hikaricp

import com.zaxxer.hikari.HikariConfig
import play.api.{Configuration}

import java.io.{File, FileReader, IOException}
import java.util.Properties

import org.apache.commons.configuration.{PropertiesConfiguration, ConfigurationConverter}
import play.Logger.ALogger

class HikariCPConfig(dbConfig: Configuration) {
  lazy val DEFAULT_DATASOURCE_NAME = "default"
  lazy val HIKARI_CP_PROPERTIES_FILE = "hikaricp.properties"
  lazy val logger : ALogger = play.Logger.of("com.edulify.play.hikaricp.HikariCPConfig")
  
  def getHikariConfig = {
    val file = new File(HIKARI_CP_PROPERTIES_FILE)
    if(file.exists()) new HikariConfig(props(file))
    else new HikariConfig(mapFromPlayConfiguration())
  }

  private def props(file: File): Properties = {
    logger.debug("Loading Hikari configuration from " + file)

    var properties = new Properties()
    try {
      properties = ConfigurationConverter.getProperties(new PropertiesConfiguration(file))
    } catch {
      case ex: IOException =>
        play.api.Logger.warn("Could not read file " + file, ex)
    }

    logger.debug("Properties: " + properties)
    properties
  }

  private def mapFromPlayConfiguration(): Properties = {
    logger.debug("Loading Hikari configuration from Play configuration.")

    val configFile = dbConfig.getString("hikaricp.file")
    if(configFile.nonEmpty) {
      logger.debug("Loading from file configured by db.default.hikaricp.file that is " + configFile)
      return props(new File(configFile.get))
    }

    val properties = new Properties()
    properties.setProperty("driverClassName",   dbConfig.getString("driver").get)
    properties.setProperty("jdbcUrl",           dbConfig.getString("url").get)
    properties.setProperty("username",          dbConfig.getString("user").get)
    properties.setProperty("password",          dbConfig.getString("password").get)

    properties.setProperty("maximumPoolSize",   maxPoolSize(dbConfig))
    properties.setProperty("minimumIdle",       minPoolSize(dbConfig))

    properties.setProperty("maxLifetime",       dbConfig.getString("maxConnectionAge").get)
    properties.setProperty("readOnly",          dbConfig.getString("defaultReadOnly").getOrElse("false"))
    properties.setProperty("acquireRetryDelay", dbConfig.getString("acquireRetryDelay").get)

    properties.setProperty("registerMbeans",    dbConfig.getString("statisticsEnabled").getOrElse("false"))
    properties.setProperty("connectionInitSql", dbConfig.getString("initSQL").get)

    logger.debug("Properties: " + properties)
    properties
  }

  private def maxPoolSize(config: Configuration) = {
    val partitionCount = config.getInt("partitionCount").getOrElse(1)
    val maxConnectionsPerPartition = config.getInt("maxConnectionsPerPartition").get
    (partitionCount * maxConnectionsPerPartition).toString
  }

  private def minPoolSize(config: Configuration) = {
    val partitionCount = config.getInt("partitionCount").getOrElse(1)
    val maxConnectionsPerPartition = config.getInt("minConnectionsPerPartition").get
    (partitionCount * maxConnectionsPerPartition).toString
  }
}
