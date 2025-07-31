package org.bmsk.lifemash.core.model.section

enum class SBSSection(val id: String) {
    POLITICS("01"),
    ECONOMICS("02"),
    SOCIAL("03"),
    INTERNATIONAL_GLOBAL("07"),
    LIFE_CULTURE("08"),
    SPORT("09"),
    ENTERTAINMENT_BROADCAST("14"),
    ;

    companion object {
        fun getById(id: String): SBSSection {
            return entries.first { it.id == id }
        }
    }
}
