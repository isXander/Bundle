package dev.isxander.bundle.config

import dev.isxander.bundle.Bundle
import org.quiltmc.json5.JsonReader
import org.quiltmc.json5.JsonWriter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object BundleConfig {
    private val options = mutableListOf<Property<*>>()

    var downloadThreads by int(
        default = 4,
        serialName = "download_threads",
        comment = "The number of threads to use when downloading mods."
    )
    var requestMetadata by boolean(
        default = true,
        serialName = "request_metadata",
        comment = "Whether or not to request mod metadata such as mod name, used for the GUI."
    )

    fun load() {
        val reader = JsonReader.json5(Bundle.LOADER_CTX.configDir.resolve("bundle.json5"))
        reader.beginObject()
        while (reader.hasNext()) {
            val name = reader.nextName()
            val option = options.find { it.serialName == name }
            if (option == null) {
                reader.skipValue()
                continue
            }
            option.read(reader)
        }
        reader.endObject()
    }

    fun save() {
        val writer = JsonWriter.json5(Bundle.LOADER_CTX.configDir.resolve("bundle.json5"))
        writer.beginObject()
        for (option in options) {
            option.comment?.let { writer.comment(it) }
            writer.name(option.comment)
            option.write(writer)
        }
        writer.endObject()
    }

    private fun int(default: Int, serialName: String, comment: String? = null) =
        Property(default, serialName, comment, JsonWriter::value, JsonReader::nextInt)

    private fun boolean(default: Boolean, serialName: String, comment: String? = null) =
        Property(default, serialName, comment, JsonWriter::value, JsonReader::nextBoolean)

    private class Property<T>(
        var value: T,
        val serialName: String,
        val comment: String? = null,
        private val writer: (JsonWriter).(T) -> Unit,
        val reader: (JsonReader).() -> T
    ) : ReadWriteProperty<BundleConfig, T> {
        init {
            options.add(this)
        }

        override fun getValue(thisRef: BundleConfig, property: KProperty<*>): T {
            return value;
        }

        override fun setValue(thisRef: BundleConfig, property: KProperty<*>, value: T) {
            this.value = value
        }

        fun write(writer: JsonWriter) {
            writer.name(serialName)
            writer.writer(value)
        }

        fun read(reader: JsonReader) {
            value = reader.reader()
        }
    }
}