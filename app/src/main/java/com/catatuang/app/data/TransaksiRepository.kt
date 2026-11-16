package com.catatuang.app.data

import kotlinx.coroutines.flow.Flow

/** Satu-satunya pintu ViewModel ke sumber data, supaya ViewModel tidak bergantung langsung ke Room. */
class TransaksiRepository(private val dao: TransaksiDao) {

    fun getSemua(): Flow<List<Transaksi>> = dao.getSemua()

    fun getById(id: Long): Flow<Transaksi?> = dao.getById(id)

    fun getAntara(awal: Long, akhir: Long): Flow<List<Transaksi>> = dao.getAntara(awal, akhir)

    suspend fun simpan(transaksi: Transaksi) {
        if (transaksi.id == 0L) dao.insert(transaksi) else dao.update(transaksi)
    }

    suspend fun hapus(transaksi: Transaksi) = dao.delete(transaksi)
}
