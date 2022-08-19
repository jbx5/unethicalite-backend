package net.unethicalite.backend.repository.entity

import java.time.Instant
import java.util.*

data class Session(
    val uuid: UUID,
    val mode: String,
    val addr: String,
    var lastUpdate: Instant = Instant.now()
)