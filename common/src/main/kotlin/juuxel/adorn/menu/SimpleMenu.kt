package juuxel.adorn.menu

import juuxel.adorn.util.getBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.menu.Menu
import net.minecraft.menu.MenuContext
import net.minecraft.menu.MenuType
import net.minecraft.menu.Slot
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

abstract class SimpleMenu(
    type: MenuType<*>,
    syncId: Int,
    private val dimensions: Pair<Int, Int>,
    override val inventory: Inventory,
    playerInventory: PlayerInventory,
    override val context: MenuContext
) : Menu(type, syncId), ContainerBlockMenu {
    init {
        val (width, height) = dimensions
        val offset = (9 - width) / 2
        checkSize(inventory, width * height)

        val slot = 18

        // Container
        for (y in 0 until height) {
            for (x in 0 until width) {
                addSlot(Slot(inventory, y * width + x, 8 + (x + offset) * slot, 17 + y * slot))
            }
        }

        // Main player inventory
        for (y in 0..2) {
            for (x in 0..8) {
                addSlot(Slot(playerInventory, x + y * 9 + 9, 8 + x * slot, 84 + y * slot))
            }
        }

        // Hotbar
        for (x in 0..8) {
            addSlot(Slot(playerInventory, x, 8 + x * slot, 142))
        }

        playSound(SoundEvents.BLOCK_BARREL_OPEN)
    }

    override fun canUse(player: PlayerEntity) =
        inventory.canPlayerUse(player)

    override fun transferSlot(player: PlayerEntity, index: Int): ItemStack {
        var result = ItemStack.EMPTY
        val slot = slots[index]

        if (slot != null && slot.hasStack()) {
            val containerSize = dimensions.first * dimensions.second
            val stack = slot.stack
            result = stack.copy()

            if (index < containerSize) {
                if (!insertItem(stack, containerSize, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(stack, 0, containerSize, false)) {
                return ItemStack.EMPTY
            }

            if (stack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }

        return result
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        playSound(SoundEvents.BLOCK_BARREL_CLOSE)
    }

    private fun playSound(soundEvent: SoundEvent?) {
        val blockEntity = context.getBlockEntity();
        if (blockEntity != null) {
            val world = blockEntity.world;
            if (world != null) {
                val vec3i = (world.getBlockState(blockEntity.pos).get(Properties.HORIZONTAL_FACING) as Direction).vector
                val d: Double = blockEntity.pos.x.toDouble() + 0.5 + vec3i.x.toDouble() / 2.0
                val e: Double = blockEntity.pos.y.toDouble() + 0.5 + vec3i.y.toDouble() / 2.0
                val f: Double = blockEntity.pos.z.toDouble() + 0.5 + vec3i.z.toDouble() / 2.0
                val randomComponent = blockEntity.world?.random?.nextFloat();
                if (randomComponent != null)
                    blockEntity.world?.playSound(null as PlayerEntity?, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, randomComponent * 0.1f + 0.9f)
            }
        }
    }
}
