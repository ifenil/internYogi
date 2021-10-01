package com.intern.yogi.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView

class Player : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colors.background ) {
                    MyPlayer()
                }
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun MyPlayer() {
        val sampleVideo = intent.getStringExtra("URL")
        val context = LocalContext.current
        val player = SimpleExoPlayer.Builder(context).build()
        val playerView = PlayerControlView(context)
        val mediaItem = sampleVideo?.let { MediaItem.fromUri(it) }
        val playWhenReady by rememberSaveable {
            mutableStateOf(true)
        }

        Column {
            Image(
                painter = rememberImagePainter(intent.getStringExtra("TrackImg")),
                contentDescription = "Track Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(600.dp)
            )

            if (mediaItem != null) {
                player.setMediaItem(mediaItem)
            }
            playerView.player = player
            LaunchedEffect(player) {
                player.prepare()
                player.playWhenReady = playWhenReady
            }
            AndroidView(factory = {
                playerView
            })
        }
    }
}