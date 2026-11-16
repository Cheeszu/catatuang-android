package com.catatuang.app.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.catatuang.app.CatatUangApplication
import com.catatuang.app.ui.beranda.BerandaViewModel
import com.catatuang.app.ui.detail.DetailViewModel
import com.catatuang.app.ui.form.FormViewModel
import com.catatuang.app.ui.riwayat.RiwayatViewModel
import com.catatuang.app.ui.ringkasan.RingkasanViewModel

/** Membuat semua ViewModel dengan Repository dari Application. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { BerandaViewModel(app().repository) }
        initializer { FormViewModel(createSavedStateHandle(), app().repository) }
        initializer { DetailViewModel(createSavedStateHandle(), app().repository) }
        initializer { RiwayatViewModel(app().repository) }
        initializer { RingkasanViewModel(app().repository) }
    }
}

private fun CreationExtras.app(): CatatUangApplication = this[APPLICATION_KEY] as CatatUangApplication
