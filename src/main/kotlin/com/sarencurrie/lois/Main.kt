package com.sarencurrie.lois

import club.minnced.discord.webhook.WebhookClient
import org.apache.commons.csv.CSVFormat
import java.io.InputStreamReader
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
    val db = DynamoDbWrapper()
    val previousLocations = db.getAllLocations()

    val newLocations = mutableListOf<Location>()
    val updatedLocations = mutableListOf<Location>()

    CsvLocationReader().getLocations().forEach {
        if (previousLocations.containsKey(it.id)) {
            if (previousLocations[it.id] != it) {
                // Update
                db.store(it)
                updatedLocations.add(it)
            }
        } else {
            // New
            db.store(it)
            newLocations.add(it)
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
