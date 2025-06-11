package com.healthapp.emotional.data.converters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.stream.JsonToken
import java.time.LocalDateTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(formatter.format(value))
        }
    }

    override fun read(`in`: JsonReader): LocalDateTime? {
        return when (`in`.peek()) {
            JsonToken.NULL -> {
                `in`.nextNull()
                null
            }
            JsonToken.NUMBER -> {
                val timestamp = `in`.nextLong()
                Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            }
            else -> {
                val dateString = `in`.nextString()
                try {
                    LocalDateTime.parse(dateString, formatter)
                } catch (e: Exception) {
                    // If parsing as ISO format fails, try parsing as timestamp
                    try {
                        val timestamp = dateString.toLong()
                        Instant.ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }
} 