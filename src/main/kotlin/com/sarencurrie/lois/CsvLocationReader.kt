package com.sarencurrie.lois

import org.apache.commons.csv.CSVFormat
import java.io.InputStreamReader
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class CsvLocationReader: LocationReader {
    override fun getLocations(): List<Location> {
        val url =
            URL("https://raw.githubusercontent.com/minhealthnz/nz-covid-data/main/locations-of-interest/august-2021/locations-of-interest.csv")
        val stream = HttpClient.newHttpClient()
            .send(HttpRequest.newBuilder(url.toURI()).GET().build(), HttpResponse.BodyHandlers.ofInputStream())
        val data = CSVFormat.DEFAULT.builder()
            .setHeader("id", "Event", "Location", "City", "Start", "End", "Advice", "LAT", "LNG")
            .build()
            .parse(InputStreamReader(stream.body()))

        return data.map {
             Location(
                it.get("id"),
                it.get("Event"),
                it.get("Location"),
                it.get("City"),
                it.get("Start"),
                it.get("End"),
                it.get("Advice"),
                it.get("LAT"),
                it.get("LNG")
            )
        }
    }
}