package net.unethicalite.backend.service

import net.unethicalite.backend.helpers.collisions.CollisionMap
import net.unethicalite.dto.regions.MappedTileDto
import net.unethicalite.dto.regions.TileFlagDto
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

//@Service
class RegionService(
    private val collisionMap: CollisionMap,
    private val storageService: StorageService
) {
    private val REGION_SIZE = 64
    private val PLANE_SIZE = 4

    fun save(tiles: List<TileFlagDto>) {
        tiles.forEach { tileFlag ->
            val region = tileFlag.region
            val x = tileFlag.x
            val y = tileFlag.y
            val z = tileFlag.z

            if (collisionMap.regions[region] == null) {
                collisionMap.createRegion(region)
            }

            if (tileFlag.isObstacle()) {
                collisionMap.set(x, y, z, 0, false)
                collisionMap.set(x, y, z, 1, false)
            } else {
                collisionMap.set(x, y, z, 0, true)
                collisionMap.set(x, y, z, 1, true)

                if (!tileFlag.north) {
                    collisionMap.set(x, y, z, 0, false)
                }

                if (!tileFlag.east) {
                    collisionMap.set(x, y, z, 1, false)
                }
            }
        }

        val bytes = collisionMap.toBytes()

        try {
            ByteArrayOutputStream(bytes.size).use { bos ->
                GZIPOutputStream(bos).use { gos ->
                    gos.write(bytes)
                    gos.finish()
                    storageService.uploadFile("regions", bos.toByteArray())
                    gos.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getFile() = storageService.getFileStream("regions")

    fun getUnmapped() = collisionMap.regions.withIndex().filter { it.value == null }.map { it.index }

    fun getMappedTiles(region: Int, z: Int): List<MappedTileDto> {
        val regionBits = collisionMap.regions[region] ?: return emptyList()
        val out = mutableListOf<MappedTileDto>()
        repeat(REGION_SIZE) { x ->
            repeat(REGION_SIZE) { y ->
                val worldX = ((region ushr 8) shl 6) + x
                val worldY = ((region and 0xFF) shl 6) + y
                val north = regionBits.get(x, y, z, 0)
                val east = regionBits.get(x, y, z, 1)
                out.add(
                    MappedTileDto(
                        worldX, worldY, z, north, east
                    )
                )
            }
        }

        return out
    }

    fun getMappedTiles(region: Int): List<MappedTileDto> {
        val out = mutableListOf<MappedTileDto>()
        repeat(PLANE_SIZE) {
            out.addAll(getMappedTiles(region, it))
        }

        return out
    }

    fun deleteInstancedRegion(region: Int) {
        collisionMap.regions[region] = null
    }
}
