package com.sarencurrie.lois

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder

fun buildEmbed(
    location: Location,
    update: Boolean
): WebhookEmbed {
    val builder = WebhookEmbedBuilder()
    try {
        builder
            .setColor(
                if (update) {
                    0x2711cf
                } else {
                    0x11cf73
                }
            )
            .setTitle(
                WebhookEmbed.EmbedTitle(
                    if (update) {
                        "Updated location of interest"
                    } else {
                        "New location of interest"
                    }, null
                )
            )
            .setAuthor(
                WebhookEmbed.EmbedAuthor(
                    "Ministry of Health",
                    null,
                    "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus/covid-19-health-advice-public/contact-tracing-covid-19/covid-19-contact-tracing-locations-interest"
                )
            )
            .setDescription(location.event)
            .addField(WebhookEmbed.EmbedField(true, "Address", location.location ?: "N/A"))
            .addField(WebhookEmbed.EmbedField(true, "Start", location.start))
            .addField(WebhookEmbed.EmbedField(true, "End", location.end))
    } catch (e: Exception) {
        println(location)
        throw e
    }
    return builder.build()
}