package  com.info.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.info.model.StockPrice

const val DATABASE_NAME = "tradewyse_db"

@Database(entities = [StockPrice::class], version = 3,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockPriceDao(): StockPriceDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getAppDataBase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, DATABASE_NAME
                    )
                         .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}
