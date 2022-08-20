package net.unethicalite.backend.repository

import net.unethicalite.backend.config.properties.SessionProperties
import net.unethicalite.backend.repository.entity.Session
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class SessionRepository(
    private val sessionProperties: SessionProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val sessions = mutableMapOf<String, Session>()

    fun findById(addr: String) = sessions[addr]

    fun newSession(addr: String) = UUID.randomUUID().run {
        toString().also {
            log.info("$it [$addr] connected")

            sessions[addr] = Session(this, addr)
        }
    }

    fun delete(addr: String) = sessions.remove(addr).let {
        log.info("[$addr] disconnected")
    }

    fun count() = sessions.size

    fun removeExpired() = sessions.entries.removeIf { (_, v) ->
        v.lastUpdate.plusMillis(sessionProperties.maxAge).isBefore(Instant.now())
    }
}