package net.unethicalite.backend.rest

import net.unethicalite.backend.repository.SessionRepository
import net.unethicalite.dto.exception.NotFoundException
import org.springframework.web.bind.annotation.*
import java.time.Instant
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/sessions")
class SessionController(
    private val sessionRepository: SessionRepository
) {
    @GetMapping
    fun count() = sessionRepository.count()

    @PostMapping
    fun newSession(@RequestParam mode: String, request: HttpServletRequest) =
        sessionRepository.newSession(mode, request.remoteAddr)

    @PostMapping("/ping")
    fun ping(@RequestParam session: String) {
        sessionRepository.findById(session)?.also {
            it.lastUpdate = Instant.now()
        } ?: throw NotFoundException("Session not found")
    }

    @DeleteMapping
    fun delete(@RequestParam session: String) {
        sessionRepository.delete(session)
    }
}