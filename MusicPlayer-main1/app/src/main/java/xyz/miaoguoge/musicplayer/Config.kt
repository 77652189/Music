package xyz.miaoguoge.musicplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer

//class MyMediaPlayer : MediaPlayer() {
//    override fun setOnCompletionListener(listener: OnCompletionListener?) {
//        super.setOnCompletionListener(listener)
//        stop()
//        release()
//        Config.setNextIndex()
//        val localFd = assetManager.openFd(Config.musicList[Config.currentMusic])
//        Config.mediaPlayer = MediaPlayer()
//        Config.mediaPlayer.setDataSource(localFd.fileDescriptor, localFd.startOffset, localFd.length)
//        Config.mediaPlayer.prepare()
//        Config.mediaPlayer.start()
//        Config.mmr.setDataSource(localFd.fileDescriptor, localFd.startOffset, localFd.length)
//        updateInfo()
//        Config.inAutoNext = true
//    }
//}

object Config {
    var mediaPlayer = MediaPlayer()
    val mmr = MediaMetadataRetriever()
    var isLoaded = false
    val musicList = listOf(
        "Anlipnes - The Empty Skies.mp3",
        "Frax.xx,illion - GASSHOW（VIII Bootleg）.mp3",
        "坂本龍一 - Merry Christmas Mr. Lawrence.mp3",
        "方拾贰-望月.mp3",
        "李蚊香 - 夢灯籠（Cover RADWIMPS）.mp3",
        "要不要买菜 - 琴师.mp3",
        "贰伍吸菸所 Smoking Area 25 - Anna.mp3"
    )
    var currentMusic = 0
    var playMode = "all"

    fun StartPlay() {
        val CurSong = Global.getSongByFilename(musicList[currentMusic])
        if(CurSong !in Global.Recent){
            if (CurSong != null) {
                Global.Recent.add(CurSong)
            }
        }
        mediaPlayer.start()
    }
    var inAutoNext = false

    fun setNextIndex() {
        when (playMode) {
            "all" -> {
                if (currentMusic < musicList.size - 1) {
                    currentMusic++
                } else {
                    currentMusic = 0
                }
            }
            "shuffle" -> {
                val indexList = (0 until currentMusic) + (currentMusic + 1 until musicList.size)
                currentMusic = indexList.random()
            }
        }
    }
}
