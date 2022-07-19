package net.unethicalite.backend.rest

import net.unethicalite.backend.repository.MaliciousRepoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/malicious-repos")
class MaliciousRepoController(
    private val maliciousRepoRepository: MaliciousRepoRepository
) {
    @GetMapping
    fun getAll() = maliciousRepoRepository.findAll().map { it.owner }
}