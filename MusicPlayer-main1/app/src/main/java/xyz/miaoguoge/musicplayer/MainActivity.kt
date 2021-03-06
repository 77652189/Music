package xyz.miaoguoge.musicplayer

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import xyz.miaoguoge.musicplayer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val assetManager = assets
        val fd = assetManager.openFd(Config.musicList[0])
        Config.mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        Config.mediaPlayer.prepare()
        Config.mmr.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)

//        Config.mediaPlayer.setOnCompletionListener {
//            Config.mediaPlayer.stop()
//            Config.mediaPlayer.release()
//            Config.setNextIndex()
//            val localFd = assetManager.openFd(Config.musicList[Config.currentMusic])
//            Config.mediaPlayer = MediaPlayer()
//            Config.mediaPlayer.setDataSource(localFd.fileDescriptor, localFd.startOffset, localFd.length)
//            Config.mediaPlayer.prepare()
//            Config.mediaPlayer.start()
//            Config.mmr.setDataSource(localFd.fileDescriptor, localFd.startOffset, localFd.length)
//            updateInfo()
//            Config.inAutoNext = true
//        }

        //遍历assets目录下的所有mp3文件
        var files = assetManager.list("") as Array<String>;
        var fd_temp = assetManager.openFd("贰伍吸菸所 Smoking Area 25 - Anna.mp3")
        var mmr_temp = MediaMetadataRetriever()
        for (file in files) {
            if(file.contains(".mp3")){
                var song_temp = Song();
                fd_temp = assetManager.openFd(file)
                mmr_temp.setDataSource(fd_temp.fileDescriptor, fd_temp.startOffset, fd_temp.length)
                song_temp.Album = mmr_temp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                song_temp.Artist = mmr_temp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                song_temp.Title = mmr_temp.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                song_temp.Filename = file
                var cover = mmr_temp.getEmbeddedPicture()
                if (cover != null) {
                    song_temp.bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.size)
                }
                Global.All.add(song_temp)
            }
        }
        //从0-6遍历All数组,前三个加入Recent,后三个加入Favor
        for (i in 0..6) {
            if (i < 3) {
                Global.Recent.add(Global.All[i])
            } else {
                Global.Favor.add(Global.All[i])
            }
        }

        binding.btnPlay.setOnClickListener {
            binding.btnPlay.visibility = Button.INVISIBLE
            binding.btnPause.visibility = Button.VISIBLE
            Config.StartPlay()
            Config.isLoaded = true
            updateInfo()
        }
        binding.btnPause.setOnClickListener {
            binding.btnPause.visibility = Button.INVISIBLE
            binding.btnPlay.visibility = Button.VISIBLE
            Config.mediaPlayer.pause()
        }
        binding.rlPlayBar.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
        //btnLocal跳转到LocalMusicActivity
        binding.btnLocal.setOnClickListener {
            val intent = Intent(this, LocalMusicActivity::class.java)
            startActivity(intent)
        }
        //btnRecent跳转到RecentPlayActivity
        binding.btnRecent.setOnClickListener {
            val intent = Intent(this, RecentPlayActivity::class.java)
            startActivity(intent)
        }
        //btnFavor跳转到MyCollectionActivity
        binding.btnFavor.setOnClickListener {
            val intent = Intent(this, MyCollectionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Config.isLoaded) {
            updateInfo()
        }
        if (Config.mediaPlayer.isPlaying) {
            binding.btnPlay.visibility = Button.INVISIBLE
            binding.btnPause.visibility = Button.VISIBLE
        } else {
            binding.btnPlay.visibility = Button.VISIBLE
            binding.btnPause.visibility = Button.INVISIBLE
        }
    }

    private fun updateInfo() {
        binding.songTitle.text = Config.mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        binding.songArtist.text = Config.mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val cover = Config.mmr.embeddedPicture
        if (cover != null) {
            val bitmap = BitmapFactory.decodeByteArray(cover, 0, cover.size)
            binding.imgCover.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Config.mediaPlayer.stop()
        Config.mediaPlayer.release()
    }
}
