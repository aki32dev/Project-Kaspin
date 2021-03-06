package com.example.kaspintest.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDB(context : Context?) : SQLiteOpenHelper(context, "local.db", null, 1) {
    override fun onCreate(DB: SQLiteDatabase?) {
        DB!!.execSQL("create Table listitems(name TEXT primary key, id TEXT, code TEXT, stock TEXT)")
    }

    override fun onUpgrade(DB: SQLiteDatabase?, p1: Int, p2: Int) {
        DB!!.execSQL("drop Table if exists listitems")
    }

    fun inputItem(name : String?, id : String?, code : String?, stock : String?): Boolean {
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("id"  , id)
        contentValues.put("code", code)
        contentValues.put("name", name)
        contentValues.put("stock", stock)
        val result = DB.insert("listitems", null, contentValues)
        return if (result == -1L) {false}
        else{true}
    }
    fun updateItem(name : String?, stock : String?): Boolean {
        val DB = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("stock", stock)
        val cursor = DB.rawQuery("Select * from listitems where name = ?", arrayOf(name))
        return if (cursor.count > 0) {
            val result = DB.update("listitems", contentValues, "name=?", arrayOf(name)).toString()
            if (result == "") {false}
            else {true}
        }
        else {false}
    }

    fun deleteItem(name : String?): Boolean {
        val DB = this.writableDatabase
        val cursor = DB.rawQuery("Select * from listitems where name = ?", arrayOf(name))
        return if (cursor.count > 0) {
            val result = DB.delete("listitems", "name=?", arrayOf(name)).toLong()
            if (result == -1L) {false}
            else {true}
        }
        else {false}
    }

    fun getItem(): Cursor {
        val DB = this.writableDatabase
        return DB.rawQuery("Select * from listitems", null)
    }
}