package org.bmsk.lifemash.data.remote.memo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.bmsk.lifemash.data.remote.memo.dto.ChecklistItemDto
import org.bmsk.lifemash.data.remote.memo.dto.CreateMemoRequest
import org.bmsk.lifemash.data.remote.memo.dto.MemoDto
import org.bmsk.lifemash.data.remote.memo.dto.SyncChecklistRequest
import org.bmsk.lifemash.data.remote.memo.dto.UpdateMemoRequest

class MemoApi(private val client: HttpClient) {

    private val base = "/api/v1/calendar"

    suspend fun getGroupMemos(groupId: String): List<MemoDto> =
        client.get("$base/$groupId/memos").body()

    suspend fun getMemo(groupId: String, memoId: String): MemoDto =
        client.get("$base/$groupId/memos/$memoId").body()

    suspend fun createMemo(groupId: String, body: CreateMemoRequest): MemoDto =
        client.post("$base/$groupId/memos") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun updateMemo(
        groupId: String,
        memoId: String,
        body: UpdateMemoRequest,
    ): MemoDto =
        client.patch("$base/$groupId/memos/$memoId") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()

    suspend fun deleteMemo(groupId: String, memoId: String): Unit =
        client.delete("$base/$groupId/memos/$memoId").body()

    suspend fun searchMemos(groupId: String, query: String): List<MemoDto> =
        client.get("$base/$groupId/memos/search") {
            url.parameters.append("q", query)
        }.body()

    suspend fun syncChecklist(
        groupId: String,
        memoId: String,
        body: SyncChecklistRequest,
    ): List<ChecklistItemDto> =
        client.put("$base/$groupId/memos/$memoId/checklist") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
}
