package net.unethicalite.backend.repository.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class MaliciousRepo(
    @Id
    val owner: String,
    val name: String
)