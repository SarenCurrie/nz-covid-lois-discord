package com.sarencurrie.lois

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.*
import com.amazonaws.services.dynamodbv2.model.*

class DynamoDbWrapper {
    private val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(System.getenv("DYNAMODB_ENDPOINT"), "ap-southeast-2"))
        .build()

    private var table: Table? = null

    private val dynamoDB = DynamoDB(client)

    fun createTable(): Table {
        val tableName = "LocationsSent"

        val tableReference = dynamoDB.getTable(tableName)
        try {
            tableReference.describe()
            this.table = tableReference
            return tableReference
        } catch (e: ResourceNotFoundException) {
            println("Table does not exist, creating...")
        }

        val table: Table = dynamoDB.createTable(
            tableName,
            listOf(
                KeySchemaElement("id", KeyType.HASH),  // Partition
            ),
            listOf(
                AttributeDefinition("id", ScalarAttributeType.S)
            ),
            ProvisionedThroughput(10L, 10L)
        )
        table.waitForActive()
        println("Success.  Table status: " + table.description.tableStatus)
        this.table = table
        return table
    }

    fun hasSent(id: String): Boolean {
        if (table == null) {
            createTable()
        }
        val foo = table!!
        val item: Item? = foo.getItem("id", id)
        return item != null
    }

    fun store(location: Location) {
        if (table == null) {
            createTable()
        }
        val item = Item().withPrimaryKey("id", location.id)
            .with("event", location.event)
            .with("location", location.location)
            .with("city", location.city)
            .with("start", location.start)
            .with("end", location.end)
            .with("information", location.information)
            .with("latitude", location.latitude)
            .with("longitude", location.longitude)

        table!!.putItem(item)
    }

    fun getAllLocations(): Map<String, Location> {
        if (table == null) {
            createTable()
        }

        val data: ItemCollection<ScanOutcome> = table!!.scan()
        return data.map{
            Pair(
                it.get("id").toString(),
                Location(
                    it.get("id").toString(),
                    it.get("event").toString(),
                    it.get("location").toString(),
                    it.get("city").toString(),
                    it.get("start").toString(),
                    it.get("end").toString(),
                    it.get("information").toString(),
                    it.get("latitude").toString(),
                    it.get("longitude").toString())
            )
        }.toMap()
    }
}