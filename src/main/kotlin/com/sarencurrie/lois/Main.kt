package com.sarencurrie.lois

import club.minnced.discord.webhook.WebhookClient
import org.apache.commons.csv.CSVFormat
import java.io.InputStreamReader
import java.lang.System.exit
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.system.exitProcess

fun main() {
    checkLocations()
    exitProcess(0)
}

fun checkLocations() {
    val url =
        URL("https://raw.githubusercontent.com/minhealthnz/nz-covid-data/main/locations-of-interest/august-2021/locations-of-interest.csv")
    val db = DynamoDbWrapper()
    val locations = db.getAllLocations()
    val stream = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(url.toURI()).GET().build(), HttpResponse.BodyHandlers.ofInputStream())
    val data = CSVFormat.DEFAULT.builder()
        .setHeader("id", "Event", "Location", "City", "Start", "End", "Information", "LAT", "LNG")
        .build()
        .parse(InputStreamReader(stream.body()))
    val newLocations = mutableListOf<Location>()
    val updatedLocations = mutableListOf<Location>()

    data.forEach {
        val id = it.get("id")
        val location = Location(
            id,
            it.get("Event"),
            it.get("Location"),
            it.get("City"),
            it.get("Start"),
            it.get("End"),
            it.get("Information"),
            it.get("LAT"),
            it.get("LNG")
        )
        if (locations.containsKey(id)) {
            if (locations[id] != location) {
                // Update
                db.store(location)
                updatedLocations.add(location)
            }
        } else {
            // New
            db.store(location)
            newLocations.add(location)
        }
    }
    print("Sending ${newLocations.size} new locations and ${updatedLocations.size} updated locations")
    val client = WebhookClient.withUrl(System.getenv("WEBHOOK_URL"))
    newLocations.chunked(10).map { client.send(it.map { l -> buildNewEmbed(l) }) }.forEach{ it.get() }
    if (updatedLocations.isNotEmpty()) {
        client.send(buildUpdateEmbed(updatedLocations)).get()
    }
    client.close()
    print("Sent ${newLocations.size} new locations and ${updatedLocations.size} updated locations")
}
