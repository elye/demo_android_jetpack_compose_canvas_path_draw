package com.example.drawpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.drawpath.MainDestinations.DRAWCUBICSCREEN
import com.example.drawpath.MainDestinations.DRAWPATHMOVETOSCREEN
import com.example.drawpath.MainDestinations.DRAWPATHSCREEN
import com.example.drawpath.MainDestinations.MAINSCREEN
import com.example.drawpath.ui.theme.DrawPathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrawPathTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}

object MainDestinations {
    const val MAINSCREEN = "mainscreen"
    const val DRAWPATHSCREEN = "drawpathscreen"
    const val DRAWPATHMOVETOSCREEN = "drawpathmovetoscreen"
    const val DRAWCUBICSCREEN = "drawcubicscreen"
}

@Composable
fun NavGraph(startDestination: String = MAINSCREEN) {
    val navController = rememberNavController()
    val actions = remember(navController) { MainActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MAINSCREEN) {
            MainScreen(actions)
        }
        composable(DRAWPATHSCREEN) {
            DrawPathArc()
        }
        composable(DRAWPATHMOVETOSCREEN) {
            DrawPathArcMoveTo()
        }
        composable(DRAWCUBICSCREEN) {
            DrawPathCubic()
        }
    }
}

class MainActions(navController: NavHostController) {
    val mainScreen: () -> Unit = {
        navController.navigate(MAINSCREEN)
    }
    val drawPathScreen: () -> Unit = {
        navController.navigate(DRAWPATHSCREEN)
    }
    val drawPathMoveToScreen: () -> Unit = {
        navController.navigate(DRAWPATHMOVETOSCREEN)
    }
    val drawPathCubicScreen: () -> Unit = {
        navController.navigate(DRAWCUBICSCREEN)
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }
}

@Composable
fun MainScreen(actions: MainActions) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            MyButton(
                onClick = { actions.drawPathScreen() },
                title = "Draw Path"
            )
            MyButton(
                onClick = { actions.drawPathMoveToScreen() },
                title = "Draw Path Move To"
            )
            MyButton(
                onClick = { actions.drawPathCubicScreen() },
                title = "Draw Cubic"
            )
        }
    }
}

@Composable
fun ColumnScope.MyButton(onClick: () -> Unit, title: String) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Text(title)
    }

}
