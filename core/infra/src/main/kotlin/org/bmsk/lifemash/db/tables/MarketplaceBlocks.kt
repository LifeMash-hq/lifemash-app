package org.bmsk.lifemash.db.tables

import org.jetbrains.exposed.sql.Table

object MarketplaceBlocks : Table("marketplace_blocks") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 100)
    val url = text("url")
    val description = text("description").nullable()
    val iconUrl = text("icon_url").nullable()
    val creatorId = uuid("creator_id").references(Users.id).nullable()
    val status = varchar("status", 20).default("PENDING")
    val createdAt = long("created_at")
    val toolsManifestUrl = text("tools_manifest_url").nullable()
    val toolDefinitions = text("tool_definitions").nullable()

    override val primaryKey = PrimaryKey(id)
}
