package juuxel.adorn.gui.screen

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.WItem
import io.github.cottonmc.cotton.gui.widget.WLabel
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.cottonmc.cotton.gui.widget.data.Alignment
import juuxel.adorn.Adorn
import juuxel.adorn.book.Book
import juuxel.adorn.book.Page
import juuxel.adorn.gui.painter.Painters
import juuxel.adorn.gui.widget.PageContainer
import juuxel.adorn.gui.widget.WBigLabel
import juuxel.adorn.gui.widget.WBookText
import juuxel.adorn.gui.widget.WCardPanel
import juuxel.adorn.gui.widget.WCenteredLabel
import juuxel.adorn.gui.widget.WPageTurnButton
import juuxel.adorn.util.Colors
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
class BookScreenDescription(book: Book) : LightweightGuiDescription() {
    init {
        val root = WPlainPanel()
        val pageCards = WCardPanel()
        val prev = WPageTurnButton(pageCards, WPageTurnButton.Direction.Previous)
        val next = WPageTurnButton(pageCards, WPageTurnButton.Direction.Next)

        pageCards.addCard(createTitlePage(book))
        for (topic in book.pages) pageCards.addCard(createPageWidget(pageCards, topic))

        root.add(prev, 49, 159, 23, 13)
        root.add(next, 116, 159, 23, 13)
        root.add(pageCards, 35, 14, 116, 145)
        root.add(WCloseButton(), 142, 14)
        root.setSize(192, 192)
        root.backgroundPainter = Painters.BOOK
        rootPanel = root
    }

    override fun addPainters() {}

    private fun createTitlePage(book: Book): WWidget {
        val result = WPlainPanel()
        result.add(WBigLabel(book.title, WLabel.DEFAULT_TEXT_COLOR, scale = book.titleScale), 0, 25, 116, 20)
        result.add(WLabel(book.subtitle, WLabel.DEFAULT_TEXT_COLOR).setAlignment(Alignment.CENTER).disableDarkmode(), 0, 45, 116, 20)
        result.add(WLabel(TranslatableText("book.byAuthor", book.author), WLabel.DEFAULT_TEXT_COLOR).setAlignment(Alignment.CENTER).disableDarkmode(), 0, 60, 116, 20)
        return result
    }

    private fun createPageWidget(pages: PageContainer, page: Page): WWidget = WPlainPanel().apply {
        val item = WItem(page.icons.map { it.stackForRender })
        add(item, 0, 0)
        add(
            WCenteredLabel(
                page.title.method_27661().method_27694 { it.setBold(true) }
                // TODO: centerVertically = true
            ),
            20, 0, 116 - 40, 20
        )
        add(WBookText(page.text, pages = pages), 4, 24, 116 - 4, 145 - 24)
    }

    private class WCloseButton : WWidget() {
        init {
            super.setSize(8, 8)
        }

        override fun setSize(x: Int, y: Int) {}

        override fun canResize() = false

        override fun paintBackground(x: Int, y: Int, mouseX: Int, mouseY: Int) {
            val texture = if (isWithinBounds(mouseX, mouseY)) ACTIVE_TEXTURE else INACTIVE_TEXTURE
            ScreenDrawing.texturedRect(x, y, 8, 8, texture, Colors.WHITE)
        }

        override fun onClick(x: Int, y: Int, button: Int) {
            if (isWithinBounds(x, y)) {
                val client = MinecraftClient.getInstance()
                client.soundManager.play(
                    PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f)
                )
                client.openScreen(null)
            }
        }

        companion object {
            private val INACTIVE_TEXTURE = Adorn.id("textures/gui/close_book_inactive.png")
            private val ACTIVE_TEXTURE = Adorn.id("textures/gui/close_book_active.png")
        }
    }
}
