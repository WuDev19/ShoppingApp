package com.example.shoppingapp.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2){
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE status_order (
                    statusId INTEGER NOT NULL PRIMARY KEY,
                    statusName TEXT NOT NULL
                )
            """.trimIndent())
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3){
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                DROP TABLE status_order
            """.trimIndent())
        }
    }

}