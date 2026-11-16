package com.catatuang.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaksiDao {

    @Query("SELECT * FROM transaksi ORDER BY tanggal DESC, id DESC")
    fun getSemua(): Flow<List<Transaksi>>

    @Query("SELECT * FROM transaksi WHERE id = :id")
    fun getById(id: Long): Flow<Transaksi?>

    @Query("SELECT * FROM transaksi WHERE tanggal BETWEEN :awal AND :akhir ORDER BY tanggal DESC, id DESC")
    fun getAntara(awal: Long, akhir: Long): Flow<List<Transaksi>>

    @Insert
    suspend fun insert(transaksi: Transaksi): Long

    @Update
    suspend fun update(transaksi: Transaksi)

    @Delete
    suspend fun delete(transaksi: Transaksi)
}
