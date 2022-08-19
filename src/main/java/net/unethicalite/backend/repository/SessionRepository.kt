package net.unethicalite.backend.repository

import net.unethicalite.backend.config.properties.SessionProperties
import net.unethicalite.backend.repository.entity.Session
import net.unethicalite.dto.exception.BadRequestException
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

    fun findById(uuid: String) = sessions[uuid]

    fun newSession(mode: String, addr: String) = UUID.randomUUID().run {
        toString().also {
            log.info("$it [$addr] connected in $mode mode")

            if (sessions.values.count { s -> s.addr == addr } > 5) {
                throw BadRequestException("Too many simultaneous connections")
            }

            sessions[it] = Session(this, mode, addr)
        }
    }

    fun delete(uuid: String) = sessions.remove(uuid).let {
        log.info("$uuid disconnected")
    }

    fun count() = sessions.size

    fun removeExpired() = sessions.entries.removeIf { (_, v) ->
        v.lastUpdate.plusMillis(sessionProperties.maxAge).isBefore(Instant.now())
    }
}