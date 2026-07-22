package com.jskaleel.vizhi_tamil

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jskaleel.vizhi_tamil.data.source.local.room.VizhiTamilDatabase
import com.jskaleel.vizhi_tamil.data.source.local.room.dao.RecentScanDao
import com.jskaleel.vizhi_tamil.data.source.local.room.entity.RecentScan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentScanDaoTest {

    private lateinit var db: VizhiTamilDatabase
    private lateinit var dao: RecentScanDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, VizhiTamilDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.recentScanDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun insertReturnsIdAndGetByIdReturnsRow() = runTest {
        val id = dao.insert(scan(timeStamp = 1000, text = "hello")).toInt()
        val loaded = dao.getById(id)
        assertEquals("hello", loaded?.text)
        assertEquals(id, loaded?.id)
    }

    @Test
    fun getAllScanIsOrderedByTimestampDescending() = runTest {
        dao.insert(scan(timeStamp = 100, text = "old"))
        dao.insert(scan(timeStamp = 300, text = "new"))
        dao.insert(scan(timeStamp = 200, text = "mid"))

        val texts = dao.getAllScan().first().map { it.text }
        assertEquals(listOf("new", "mid", "old"), texts)
    }

    @Test
    fun updateTextChangesStoredText() = runTest {
        val id = dao.insert(scan(timeStamp = 1, text = "before")).toInt()
        dao.updateText(id, "after")
        assertEquals("after", dao.getById(id)?.text)
    }

    @Test
    fun deleteByIdsRemovesRows() = runTest {
        val id1 = dao.insert(scan(timeStamp = 1, text = "a")).toInt()
        val id2 = dao.insert(scan(timeStamp = 2, text = "b")).toInt()

        dao.deleteByIds(listOf(id1))

        assertNull(dao.getById(id1))
        assertEquals("b", dao.getById(id2)?.text)
    }

    private fun scan(timeStamp: Long, text: String) = RecentScan(
        filePath = "/path/$timeStamp.jpg",
        timeStamp = timeStamp,
        text = text,
        accuracy = 90,
    )
}
