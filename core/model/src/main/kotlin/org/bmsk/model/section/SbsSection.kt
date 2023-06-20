package org.bmsk.model.section

enum class SbsSection(val id: String) {
    POLITICS("01"),
    ECONOMICS("02"),
    SOCIAL("03"),
    LIFE_CULTURE("07"),
    INTERNATIONAL_GLOBAL("08"),
    SPORT("09"),
    ENTERTAINMENT_BROADCAST("14");

    companion object {
        fun getById(id: String): SbsSection {
            return values().first { it.id == id }
        }
    }
}