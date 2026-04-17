package com.example.shoppingapp.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE status_order (
                    statusId INTEGER NOT NULL PRIMARY KEY,
                    statusName TEXT NOT NULL
                )
            """.trimIndent()
            )
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                DROP TABLE status_order
            """.trimIndent()
            )
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE orders ADD shopId INTEGER NOT NULL
            """.trimIndent()
            )
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE orders ADD shopGhnId INTEGER NOT NULL
            """.trimIndent()
            )
        }
    }

    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                ALTER TABLE orders ADD ngayChinhSua INTEGER NOT NULL
            """.trimIndent()
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_product_details_tenDetail ON product_details(tenDetail)
            """.trimIndent()
            )
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS index_orders_userId_orderStatus_ngayChinhSua 
                ON orders(userId, orderStatus, ngayChinhSua DESC)
            """.trimIndent()
            )
        }
    }

}