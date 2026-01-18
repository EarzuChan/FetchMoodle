package lib.fetchmoodle

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

class MoodleFetcherConfig()

class MoodleContext(
    val httpClient: HttpClient,
    var baseUrl: String? = null,
    var sesskey: String? = null,
    var moodleSession: String? = null
) {
    private companion object {
        const val TAG = "MoodleFetcher"
    }

    val isSessionDataSet: Boolean; get() = !baseUrl.isNullOrEmpty() && !moodleSession.isNullOrEmpty() && !sesskey.isNullOrEmpty() // 缺一不可

    fun HttpRequestBuilder.injectMoodleSession() {
        moodleSession?.let {
            cookie("MoodleSession", it)
            MoodleLog.d(TAG, "已注入Moodle会话")
        }
    }

    fun cleanSessionData() {
        baseUrl = null
        sesskey = null
        moodleSession = null
    }
}

class MoodleFetcher(moodleFetcherConfig: MoodleFetcherConfig = MoodleFetcherConfig()) {
    private val moodleContext = MoodleContext(HttpClient(CIO))

    // 使用已有的会话数据登录
    fun setSessionData(baseUrl: String, sesskey: String, moodleSession: String) {
        moodleContext.baseUrl = baseUrl
        moodleContext.sesskey = sesskey
        moodleContext.moodleSession = moodleSession
    }

    // 相当于客户端侧登出
    fun clearSessionData() = moodleContext.cleanSessionData()

    // TIPS：通用方法
    suspend fun <RESULT_TYPE> execute(operation: MoodleOperation<RESULT_TYPE>): MoodleResult<RESULT_TYPE> = with(operation) {
        moodleContext.execute()
    }

    suspend fun login(baseUrl: String, username: String, password: String): MoodleResult<Unit> = execute(LoginOperation(baseUrl, username, password))

    suspend fun getGrades() = execute(GradesQuery())

    companion object {
        private const val TAG = "MoodleFetcher"
    }
}

class MoodleException(message: String, cause: Throwable? = null) : Exception(message, cause)