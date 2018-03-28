package net.hinyari.plugin.storagebox

import java.util.logging.Level
import java.util.logging.Logger

class Logger constructor(private val logger: Logger) {
    
    
    fun info(msg: String) {
        logger.log(Level.INFO, msg)
    }
    
    fun warn(msg: String) {
        logger.log(Level.WARNING, msg)
    }
    
    fun log(level: Level, msg: String) {
        logger.log(level, msg)
    }
    
    
    
    
    
}
