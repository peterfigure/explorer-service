package io.provenance.explorer.service

import io.provenance.explorer.config.ExplorerProperties
import io.provenance.explorer.domain.core.logger
import io.provenance.explorer.domain.entities.SpotlightCacheRecord
import io.provenance.explorer.domain.models.explorer.Spotlight
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class CacheService(private val explorerProperties: ExplorerProperties) {

    protected val logger = logger(CacheService::class)

    fun addSpotlightToCache(spotlightResponse: Spotlight) = SpotlightCacheRecord.insertIgnore(spotlightResponse)

    fun getSpotlight() = transaction { SpotlightCacheRecord.getSpotlight() }
}
