package org.bmsk.lifemash.group

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/calendar/groups")
class Groups {

    @Serializable
    @Resource("join")
    class Join(val parent: Groups = Groups())

    @Serializable
    @Resource("{groupId}")
    data class ById(val parent: Groups = Groups(), val groupId: String)
}
