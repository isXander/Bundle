package dev.isxander.bundle.gui

import java.awt.Dimension
import java.awt.Rectangle
import javax.swing.JFrame
import javax.swing.JProgressBar
import javax.swing.WindowConstants

class LoadingGui : JFrame("Bundle Updater") {
    private val progressBar: JProgressBar
    private val modProgress = mutableMapOf<Int, Long>()

    init {
        setSize(400, 20)
        //iconImage = getResourceImage("/bundle.png")
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        isResizable = false

        progressBar = JProgressBar(0, 1)
        progressBar.value = 0
        progressBar.bounds = Rectangle(0, 0, 400, 20)
        progressBar.preferredSize = Dimension(400, 20)
        add(progressBar)

        pack()

        setLocationRelativeTo(null)
    }

    fun startDownload(totalSize: Long) {
        progressBar.maximum = totalSize.toInt()
        progressBar.value = 0
    }

    fun setModProgress(modIndex: Int, bytesSent: Long) {
        modProgress[modIndex] = bytesSent
        progressBar.value = modProgress.values.sum().toInt()
    }
}