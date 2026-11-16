package com.catatuang.app.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.catatuang.app.ui.beranda.BerandaScreen
import com.catatuang.app.ui.detail.DetailTransaksiScreen
import com.catatuang.app.ui.form.FormTransaksiScreen
import com.catatuang.app.ui.riwayat.RiwayatScreen
import com.catatuang.app.ui.ringkasan.RingkasanScreen

object Rute {
    const val BERANDA = "beranda"
    const val RIWAYAT = "riwayat"
    const val RINGKASAN = "ringkasan"
    const val FORM = "form?id={id}"
    const val DETAIL = "detail/{id}"

    fun form(id: Long = 0L) = "form?id=$id"
    fun detail(id: Long) = "detail/$id"
}

private data class TabUtama(val rute: String, val label: String, val icon: ImageVector)

private val tabUtama = listOf(
    TabUtama(Rute.BERANDA, "Beranda", Icons.Filled.Home),
    TabUtama(Rute.RIWAYAT, "Riwayat", Icons.AutoMirrored.Filled.ReceiptLong),
    TabUtama(Rute.RINGKASAN, "Ringkasan", Icons.Filled.PieChart),
)

@Composable
fun CatatUangApp(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val ruteSekarang = backStack?.destination?.route
    val tampilBottomBar = tabUtama.any { it.rute == ruteSekarang }

    Scaffold(
        // Inset status bar diurus oleh TopAppBar di tiap layar, jadi Scaffold luar tidak memakainya.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (tampilBottomBar) {
                NavigationBar {
                    tabUtama.forEach { tab ->
                        NavigationBarItem(
                            selected = ruteSekarang == tab.rute,
                            onClick = {
                                navController.navigate(tab.rute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rute.BERANDA,
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).consumeWindowInsets(padding),
        ) {
            composable(Rute.BERANDA) {
                BerandaScreen(
                    onTambah = { navController.navigate(Rute.form()) },
                    onBukaTransaksi = { navController.navigate(Rute.detail(it)) },
                )
            }
            composable(Rute.RIWAYAT) {
                RiwayatScreen(onBukaTransaksi = { navController.navigate(Rute.detail(it)) })
            }
            composable(Rute.RINGKASAN) {
                RingkasanScreen()
            }
            composable(
                Rute.FORM,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L }),
            ) {
                FormTransaksiScreen(
                    onSelesai = { navController.popBackStack() },
                    onKembali = { navController.popBackStack() },
                )
            }
            composable(
                Rute.DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) {
                DetailTransaksiScreen(
                    onEdit = { navController.navigate(Rute.form(it)) },
                    onKembali = { navController.popBackStack() },
                )
            }
        }
    }
}
