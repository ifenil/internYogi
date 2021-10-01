package com.intern.yogi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.intern.yogi.data.Track
import com.intern.yogi.player.Player
import com.intern.yogi.repo.TrackRepo
import com.intern.yogi.ui.theme.Primary
import com.intern.yogi.viewmodel.TrackViewModel
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
            .wrapContentSize(Alignment.Center)
    ) {
        BookList()
    }
}

@Composable
fun FavouriteScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
            .wrapContentSize(Alignment.Center)
    ) {
        FavList()
    }
}

@Composable
fun BookList(
    trackViewModel: TrackViewModel = viewModel(
        factory = TrackViewModelFactory(TrackRepo())
    )
) {

    when (val TrackList = trackViewModel.booksStateFlow.asStateFlow().collectAsState().value) {

        is OnError -> {
            Text(text = "Please try after sometime")
        }

        is OnSuccess -> {
            val listOfTrack = TrackList.querySnapshot?.toObjects(Track::class.java)
            listOfTrack?.let {
                Column {
                    Text(
                        text = "Intern Yogi",
                        color = White,
                        style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn() {
                        items(listOfTrack) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                TrackDetails(it)
                            }
                        }
                    }

                }
            }
        }
    }
}

@SuppressLint("CommitPrefEdits")
@OptIn(ExperimentalAnimationApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun TrackDetails(track: Track) {
    val context = LocalContext.current
    val selected = remember { mutableStateOf(false) }
    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    sp.edit().putBoolean(track.id,true);

    var showTrackDescription by remember { mutableStateOf(false) }
    val TrackImageSize by animateDpAsState(
        targetValue =
        if (showTrackDescription) 50.dp else 80.dp
    )

    Column(modifier = Modifier.clickable {
        showTrackDescription = showTrackDescription.not()
    }) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberImagePainter(track.artistImage),
                contentDescription = "Artist Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(TrackImageSize)
            )

            Column {
                Text(
                    text = track.artistName, style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Text(
                    text = track.id, style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                )
            }
            Image(painter = painterResource(R.drawable.ic_play),
                contentDescription = "Play",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(CenterVertically)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Play",
                        onClick = {
                            val intent = Intent(context, Player::class.java)
                            val extras: Bundle? = intent.getExtras()
                            intent.putExtra("URL",track.trackUrl)
                            intent.putExtra("TrackImg",track.image)
                            startActivity(context, intent, extras)
                        }
                    )
            )

            selected.value = !selected.value
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterVertically),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (sp.getBoolean(track.id,false)) Yellow else Color.White ),

                onClick = { sp.edit().putBoolean(track.id,true).apply();}) {
                Text(text = "Like", color = Black)
            }
        }

        AnimatedVisibility(visible = showTrackDescription) {
            Text(
                text = "Artist By: "+track.id, style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            )
        }
    }

}

class TrackViewModelFactory(private val trackRepo: TrackRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            return TrackViewModel(trackRepo) as T
        }

        throw IllegalStateException()
    }
}

@Composable
fun FavList(
    trackViewModel: TrackViewModel = viewModel(
        factory = FavTrackViewModelFactory(TrackRepo())
    )
) {

    when (val TrackList = trackViewModel.booksStateFlow.asStateFlow().collectAsState().value) {

        is OnError -> {
            Text(text = "Please try after sometime")
        }

        is OnSuccess -> {
            val listOfTrack = TrackList.querySnapshot?.toObjects(Track::class.java)
            listOfTrack?.let {
                Column {
                    Text(
                        text = "Intern Yogi's Favourite",
                        color = White,
                        style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(listOfTrack) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                FavTrackDetails(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("CommitPrefEdits")
@OptIn(ExperimentalAnimationApi::class, coil.annotation.ExperimentalCoilApi::class)
@Composable
fun FavTrackDetails(track: Track) {
    val context = LocalContext.current
    val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    var showTrackDescription by remember { mutableStateOf(false) }
    val TrackImageSize by animateDpAsState(
        targetValue =
        if (showTrackDescription) 50.dp else 80.dp
    )
    if(sp.getBoolean(track.id,false)){
    Column(modifier = Modifier.clickable {
        showTrackDescription = showTrackDescription.not()
    }) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberImagePainter(track.artistImage),
                contentDescription = "Artist Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(TrackImageSize)
            )

            Column {
                Text(
                    text = track.artistName, style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Text(
                    text = track.id, style = TextStyle(
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp
                    )
                )
            }
            Image(painter = painterResource(R.drawable.ic_play),
                contentDescription = "Play",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .align(CenterVertically)
                    .clickable(
                        enabled = true,
                        onClickLabel = "Play",
                        onClick = {
                            context.startActivity(Intent(context, Player::class.java))
                        }
                    )
            )

            val selected = remember { mutableStateOf(false) }
            selected.value = !selected.value
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterVertically),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Yellow
                ),

                onClick = { sp.edit().putBoolean(track.id, false).apply(); }) {
                Text(text = "UnLike", color = Black)
            }
        }

        AnimatedVisibility(visible = showTrackDescription) {
            Text(
                text = "Artist By: " + track.id, style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
            )
        }
    }
    }

}

class FavTrackViewModelFactory(private val trackRepo: TrackRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackViewModel::class.java)) {
            return TrackViewModel(trackRepo) as T
        }

        throw IllegalStateException()
    }
}
