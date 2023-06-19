package net.critika.application.game.response

import kotlinx.serialization.Serializable
import net.critika.application.Response
import net.critika.application.player.response.PlayerResponse
import net.critika.application.stage.response.StageResponse
import net.critika.application.vote.DayVoteResponse

@Serializable
data class GameResponse(
    val id: String,
    val date: String,
    val host: PlayerResponse?,
    val players: List<PlayerResponse>,
    val currentStage: StageResponse? = null,
    val nominates: List<PlayerResponse> = listOf(),
    val votes: List<DayVoteResponse> = listOf(),
    val mafiaShot: PlayerResponse? = null,
    val detectiveCheck: PlayerResponse? = null,
    val donCheck: PlayerResponse? = null,
    val playersEliminated: List<PlayerResponse> = listOf(),
    val bestMove: List<PlayerResponse>? = listOf(),
) : Response
