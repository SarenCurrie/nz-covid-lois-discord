package com.sarencurrie.lois

data class Location(
    val id: String,
    val event: String,
    val location: String?,
    val city: String?,
    val start: String,
    val end: String,
    val information: String,
    val latitude: String?,
    val longitude: String?
) {
    val omicron: Boolean
        get() = information.contains("omicron") || information.contains("Omicron")

    val auckland: Boolean
        get() = city?.equals("Auckland") ?: false

    override fun equals(other: Any?): Boolean {
        return other != null
                && other is Location
                && other.id == this.id
                && other.event == this.event
                && other.location == this.location
                && other.start == this.start
                && other.end == this.end
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + event.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + (city?.hashCode() ?: 0)
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }
}