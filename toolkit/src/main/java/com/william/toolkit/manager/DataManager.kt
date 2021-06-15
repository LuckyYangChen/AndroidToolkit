package com.william.toolkit.manager

import com.william.toolkit.ToolkitPanel
import com.william.toolkit.bean.ApiRecordBean
import com.william.toolkit.db.ToolkitDatabase
import com.william.toolkit.ext.logd
import com.william.toolkit.ext.logi
import com.william.toolkit.ext.logw
import kotlin.concurrent.thread


/**
 * @author William
 * @date 2021/6/13 16:11
 * Class Comment：数据管理类
 */
object DataManager {

    // 最多存储1万条，超过后就移除头部数据
    private const val cacheCount = 10000

    /**
     * 数据库表[com.william.toolkit.db.RecordDao]操作对象
     */
    private val dao = ToolkitDatabase.getInstance(ToolkitPanel.appContext).getRecordDao()

    /**
     * 保存单条记录
     */
    @JvmStatic
    fun saveRecord(bean: ApiRecordBean?) {
        if (!ToolkitPanel.isDebugMode) {
            return
        }
        if (bean == null) {
            return
        }
        thread {
            removeTopRecord()
            dao.insertRecord(bean)
            "record save success：${bean.url}".logi()
        }
    }

    /**
     * 保存记录列表
     */
    @JvmStatic
    fun saveRecordList(list: List<ApiRecordBean>?) {
        if (!ToolkitPanel.isDebugMode) {
            return
        }
        if (list.isNullOrEmpty()) {
            return
        }
        thread {
            removeTopRecord(list.size)
            dao.insertRecordList(list)
            "record save success：${list.size}".logi()
        }
    }

    private fun removeTopRecord(offset: Int = 1) {
        val totalCount = dao.queryTotalRecord()
        "record totalCount: $totalCount".logd()
        if (totalCount >= cacheCount) {
            dao.removeHeadRecord(offset)
            "top record removed: $offset".logw()
        }
    }

    /**
     * 读取数据列表
     */
    internal fun getRecordList(index: Long): List<ApiRecordBean> {
        return dao.queryLimitRecord(index)
    }

    /**
     * 清除数据表
     */
    internal fun deleteAllRecord(): Boolean {
        val rows = dao.deleteAllRecord()
        val flag = rows > 0
        "record delete rows: $rows".logi()
        return flag
    }

}