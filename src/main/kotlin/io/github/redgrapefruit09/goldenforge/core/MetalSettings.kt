package io.github.redgrapefruit09.goldenforge.core

import com.redgrapefruit.datapipe.kotlin.PipeResourceLoader
import com.redgrapefruit.datapipe.kotlin.Pipeline
import com.redgrapefruit.datapipe.kotlin.PipelineOutput
import io.github.redgrapefruit09.goldenforge.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.util.Identifier
import java.io.InputStream
import kotlin.random.Random

// Metal configurations with data unique to a specific type
// Loaded via KotlinX.Serialization, Minecraft's resource system, Fabric's resource API and DataPipe API

@Serializable
data class MetalSettings(
    val production: MetalProductionSettings,
)

@Serializable
data class MetalProductionSettings(
    val oreDrops: List<MetalOreDrop>,                          // a list of all MetalOreDrops applicable
    val cleanFragment: String,                                 // stringified ID of the clean fragment of this metal
    val reinforcementInputs: List<FragmentReinforcementInput>, // all required inputs for reinforcements
    val reinforcedFragment: String,                            // stringified ID of the reinforced fragment of this metal
    val gloves: MetalGloveSettings,                            // the gloves JSON block
    val container: MetalContainerSettings,                     // the container JSON block
    val materialTemperature: Int                               // the temperature that the raw material gives
)

/**
 * Represents one of the items an ore may drop
 */
@Serializable
data class MetalOreDrop(
    val droppedItem: String,   // the stringified ID of the dropped item, for example, minecraft:iron_ore
    val chance: Int,           // the chance of this drop happening
    val dropCount: Range,      // the range representing the amount of the dropped item
    val doubledDropChance: Int, // the chance of the drop count doubling
)

/**
 * A miscellaneous range from minimum inclusively to maximum **inclusively**.
 *
 * To pick from this [Range] randomly, use the [pick] function
 */
@Serializable
data class Range(
    val min: Int, // minimum value
    val max: Int,  // maximum value
) {
    /**
     * Picks a number from this range
     */
    fun pick(): Int {
        return Random.nextInt(min, max + 1)
    }
}

@Serializable
data class FragmentReinforcementInput(
    val id: String, // the stringified ID of the input item
    val amount: Int, // the required amount of the item
)

@Serializable
data class MetalGloveSettings(
    val name: String,   // the name of the gloves
    val withhold: Int,  // the temperature withheld by using them in degrees
    val useMinutes: Int // the max use minutes before they run out
) {
    companion object {
        // Some settings are stored outside JSONs, thus they are here:
        private val LOCAL_STORAGE = mutableMapOf(
            "iron" to MetalGloveSettings("Iron", 1500, 180),
            "golden" to MetalGloveSettings("Gold", 2000, 240),
            "diamond" to MetalGloveSettings("Diamond", 3000, 360),
            "netherite" to MetalGloveSettings("Netherite", 4000, 480)
        )

        fun get(settings: MetalSettings?, id: String): MetalGloveSettings? {
            return settings?.production?.gloves ?: LOCAL_STORAGE[id]
        }
    }
}

@Serializable
data class MetalContainerSettings(
    val name: String,    // the name of the container
    val withhold: Int,   // the temperature withheld by using them in degrees
    val capacity: Int,   // the litre capacity of the container
    val useMinutes: Int  // the max use minutes before the container runs out
) {
    companion object {
        // Some container settings are stored outside JSONs, thus they are here:
        private val LOCAL_STORAGE = mutableMapOf(
            "iron" to MetalContainerSettings("Iron", 2000, 3, 60),
            "golden" to MetalContainerSettings("Gold", 2500, 5, 120),
            "diamond" to MetalContainerSettings("Diamond", 3500, 7, 180),
            "netherite" to MetalContainerSettings("Netherite", 4000, 10, 240)
        )

        fun get(settings: MetalSettings?, id: String): MetalContainerSettings? {
            return settings?.production?.container ?: LOCAL_STORAGE[id]
        }
    }
}

/**
 * A loader for [MetalSettings] that uses the KotlinX.Serialization API for loading the JSONs
 */
object MetalSettingsLoader : PipeResourceLoader<MetalSettings>() {
    override val pipeline: Pipeline<MetalSettings> = Pipeline
        .builder<MetalSettings>()
        .underId("metal_settings_loader".id)
        .storedIn("config")
        .filterByExtension(".json")
        .build()

    override fun load(stream: InputStream, id: Identifier): PipelineOutput {
        // Load via KotlinX.Serialization
        val raw = stream.useAndRead()
        val settings = Json.decodeFromString(MetalSettings.serializer(), raw)
        // Cut the name
        val name = id.toString().remove("$MOD_ID:config/").remove(".json")

        return PipelineOutput(settings, name.id)
    }

    fun getResource(id: Identifier): MetalSettings = pipeline.getOrThrow(id)
}
