package lib.fetchmoodle

interface MoodleRes<RES_TYPE> {
    val type: String
    val data: RES_TYPE
}

interface MoodleResParser<RES_TYPE, RAW_TYPE> {
    fun parse(raw: RAW_TYPE): MoodleRes<RES_TYPE>
}