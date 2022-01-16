package io.github.redgrapefruit09.goldenforge.util

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.state.property.IntProperty
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.crash.CrashException
import net.minecraft.util.crash.CrashReport
import java.io.InputStream
import java.io.OutputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.random.Random
import kotlin.reflect.KProperty

// A bunch of miscellaneous utilities. Larger stuff is split into its own files

// ID utils
const val MOD_ID = "goldenforge"

inline val String.id: Identifier
    get() = Identifier(MOD_ID, this)

inline val String.combinedId: Identifier
    get() = Identifier(this)

// File utils

/**
 * Opens the [InputStream], reads all of its bytes, closes the [InputStream] and returns the decoded string.
 */
fun InputStream.useAndRead(): String {
    use {
        return readBytes().decodeToString()
    }
}

/**
 * The opposite of [useAndRead] allowing for writing data to an [OutputStream]
 */
fun OutputStream.useAndWrite(message: String) {
    use {
        write(message.encodeToByteArray())
    }
}

// String utils

val String.Companion.empty // an empty string
    get() = ""

/**
 * Removes a [fragment] from a string and returns the resulting string
 */
fun String.remove(fragment: String): String = replace(fragment, String.empty)

// Other

fun intProperty(name: String): IntProperty = IntProperty.of(name, 0, 100)

fun crash(message: String, cause: Throwable): CrashException {
    return CrashException(CrashReport(message, cause))
}

val itemSettings: Item.Settings = Item.Settings().group(ItemGroup.MISC)

inline fun withChance(chance: Int, action: () -> Unit) {
    if (Random.nextInt(101) <= chance) action()
}

inline val Item.stack: ItemStack
    get() = ItemStack(this)

inline fun onServer(action: () -> Unit) {
    if (FabricLoader.getInstance().environmentType == EnvType.SERVER) action()
}

inline fun onClient(action: () -> Unit) {
    if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) action()
}

inline fun fallbackGetBlockPropertyDelegate(
    context: ScreenHandlerContext,
    fallback: () -> PropertyDelegate,
): PropertyDelegate {
    var result = SyncedGuiDescription.getBlockPropertyDelegate(context)

    // If an empty result is made in case of failure, call the fallback
    if (result.size() == 0) result = fallback()

    return result
}

// A basic assertion utility. No extra context provided
fun check(value: Boolean) {
    if (!value) throw RuntimeException()
}

fun isFuel(stack: ItemStack) = AbstractFurnaceBlockEntity.canUseAsFuel(stack)

// Two-way conversions for translation keys and ids

fun Identifier.toTranslationKey(category: String): String {
    return "$category.$namespace.$path"
}

fun String.translationToIdentifier(): Identifier {
    val splits = split(".")
    check(splits.size == 3)
    return Identifier(splits[1], splits[2])
}



fun MutableList<Text>.newLine() {
    add(LiteralText(""))
}

// Access to the temperature property
interface PlayerEntityMixinAccess {
    var temperature: Int
}

inline var PlayerEntity.temperature: Int
    get() {
        return (this as PlayerEntityMixinAccess).temperature
    }
    set(value) {
        (this as PlayerEntityMixinAccess).temperature = value
    }

// A normal registry
interface IRegistry {
    fun initialize()
}

// A client-sided registry
interface IClientRegistry {
    fun initializeClient()
}

// Invocation delegate
class InvocationDelegate<A, B>(private val invoke: () -> B): ReadOnlyProperty<A, B> {
    override fun getValue(thisRef: A, property: KProperty<*>): B {
        return invoke()
    }
}

fun <A, B> invocation(invoke: () -> B) = InvocationDelegate<A, B>(invoke)

fun ItemStack.increment() {
    increment(1)
}

fun ItemStack.decrement() {
    decrement(1)
}

fun Item.Settings.notStackable(): Item.Settings {
    return maxCount(1)
}

class ModDamageSource(name: String) : DamageSource(name)

/**
 * A utility for associating data for items in their [ItemStack]s.
 */
interface ItemData {
    /**
     * The NBT sub-category to store all the data in.
     */
    val nbtCategory: String

    /**
     * Read in the data here.
     */
    fun readNbt(nbt: NbtCompound)

    /**
     * Write back the data here.
     */
    fun writeNbt(nbt: NbtCompound)

    /**
     * Clear the NBT of all data. This is required before [ItemData] synchronization.
     */
    fun clearNbt(nbt: NbtCompound)

    /**
     * Used to verify if this is a first-time write
     */
    fun verifyNbt(nbt: NbtCompound): Boolean

    companion object {
        /**
         * Prepares the [ItemData] for further use.
         */
        fun <T> prepare(instance: T, stack: ItemStack) where T : ItemData {
            val nbt = stack.getOrCreateSubNbt(instance.nbtCategory)

            // Ensure safety if no info is yet present
            if (!instance.verifyNbt(nbt)) {
                instance.clearNbt(nbt)
                instance.writeNbt(nbt)
            }

            instance.readNbt(nbt)
        }

        /**
         * Fully initializes the [ItemData] from an [ItemStack] with a factory (constructor reference).
         */
        inline fun <T> get(factory: () -> T, stack: ItemStack): T where T : ItemData {
            val instance = factory.invoke()
            prepare(instance, stack)
            return instance
        }
    }
}

/**
 * This synchronizes the changes made to the [ItemData] instance.
 *
 * **After any set-change, the sync must be called**.
 *
 * As an alternative, using the inline [use] utility is preferred.
 */
fun ItemData.sync(stack: ItemStack) {
    // Get or create the sub-NBT category
    val nbt = stack.getOrCreateSubNbt(nbtCategory)
    // Clear the NBT, then write the current state back into the NBT
    clearNbt(nbt)
    writeNbt(nbt)
}

/**
 * A utility that runs the lambda action and then synchronizes the changes.
 */
inline fun <T> T.use(stack: ItemStack, action: T.() -> Unit) where T : ItemData {
    action(this)
    sync(stack)
}
