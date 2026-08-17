package com.example.codebox.domain


data class Award(
    val key: String = "",
    val name: String = "",
    val rankDescriptions: List<String> = emptyList(),
    val iconKey: String = "",
    val condition: AwardCondition = AwardCondition.COUNT,
    val order: Int = 0,
    val maxRank: Int = 1,
    val rankThresholds: List<Int> = emptyList()
) {
    fun getThresholdForRank(rank: Int): Int {
        if (rankThresholds.isEmpty() || rank > rankThresholds.size){
            return Int.MAX_VALUE
        }
        return rankThresholds[rank-1]
    }

    fun getNextThreshold(currentRank: Int): Int{
        val nextRank = currentRank + 1
        return getThresholdForRank(nextRank)
    }

    fun getRankDescription(rank: Int): String {
        return if ( rankDescriptions.isNotEmpty() && rank <= rankDescriptions.size){
            rankDescriptions[rank - 1]
        } else {
            ""
        }
    }
}