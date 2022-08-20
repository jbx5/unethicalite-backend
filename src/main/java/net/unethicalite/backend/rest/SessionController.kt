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
    fun newSession(request: HttpServletRequest) =
        sessionRepository.newSession(request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr)

    @PostMapping("/ping")
    fun ping(request: HttpServletRequest) {
        sessionRepository.findById(request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr)?.also {
            it.lastUpdate = Instant.now()
        } ?: throw NotFoundException("Session not found")
    }

    @DeleteMapping
    fun delete(request: HttpServletRequest) {
        sessionRepository.delete(request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr)
    }
}