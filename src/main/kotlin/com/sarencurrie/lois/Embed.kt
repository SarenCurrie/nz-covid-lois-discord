package com.sarencurrie.lois

import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder

private val UPDATE_COLOR = 0x2711cf
private val NEW_COLOUR = 0x11cf73

private val MOH_WEBSITE = "https://www.health.govt.nz/our-work/diseases-and-conditions/covid-19-novel-coronavirus/covid-19-health-advice-public/contact-tracing-covid-19/covid-19-contact-tracing-locations-interest"

private enum class Colors(val rgb: Int) {
    UPDATE(0x2711cf),
    AUCKLAND(0xe8bd54),
    OMICRON(0x541b16),
    DEFAULT(0x11cf73),
}

fun buildNewEmbed(
    location: Location,
): WebhookEmbed {
    val builder = WebhookEmbedBuilder()
    return try {
        val color = if (location.auckland) {
            if (location.omicron) {
                Colors.OMICRON
            } else {
                Colors.AUCKLAND
            }
        } else {
            Colors.DEFAULT
        }

        builder
            .setColor(color.rgb)
            .setTitle(
                WebhookEmbed.EmbedTitle("New location of interest", null)
            )
            .setAuthor(
                WebhookEmbed.EmbedAuthor(
                    "Ministry of Health",
                    null,
                        MOH_WEBSITE
                )
            )
            .setDescription(location.event)
            .addField(WebhookEmbed.EmbedField(true, "Address", location.location ?: "N/A"))
            .addField(WebhookEmbed.EmbedField(true, "Start", location.start))
            .addField(WebhookEmbed.EmbedField(true, "End", location.end))
            if (location.omicron) {
                builder.addField(WebhookEmbed.EmbedField(true, "Variant", "Omicron"))
            }
            builder.build()
    } catch (e: Exception) {
        println(location)
        throw e
    }
}

fun buildUpdateEmbed(locations: List<Location>): WebhookEmbed {
    val builder = WebhookEmbedBuilder()
    return try {
        builder
                .setColor(UPDATE_COLOR)
                .setTitle(
                        WebhookEmbed.EmbedTitle("Updated location(s) of interest", MOH_WEBSITE)
                )
                .setAuthor(
                        WebhookEmbed.EmbedAuthor(
                                "Ministry of Health",
                                null,
                                MOH_WEBSITE
                        )
                )
                .setDescription("**If you have been to any of these locations, check the MoH website for more details:**\n\n" +
                        locations
                        .map { it.event }
                        .reduce { acc, s -> acc + "\n" + s })
                .build()
    } catch (e: Exception) {
        println(locations)
        throw e
    }
}
