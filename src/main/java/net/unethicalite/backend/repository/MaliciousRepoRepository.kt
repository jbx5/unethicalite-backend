package net.unethicalite.backend.repository

import net.unethicalite.backend.repository.entity.MaliciousRepo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MaliciousRepoRepository : CrudRepository<MaliciousRepo, String>