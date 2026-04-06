package com.dating.home.presentation.chat.mappers

import com.dating.home.domain.models.DateProposalLocation
import com.dating.home.domain.models.DateProposalStatus
import kotlinx.serialization.Serializable
import com.dating.home.domain.models.MessageType
import com.dating.home.domain.models.MessageWithSender
import com.dating.home.domain.models.ReactionSummary
import com.dating.home.presentation.chat.model.DateProposalUi
import com.dating.home.presentation.chat.model.LocationMessageUi
import com.dating.home.presentation.chat.model.MessageUi
import com.dating.home.presentation.chat.util.DateUtils
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json

@Serializable
private data class DateProposalLocationDto(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null
)

@Serializable
private data class DateProposalContentDto(
    val dateTime: String,
    val location: DateProposalLocationDto,
    val status: String,
    val previousProposalMessageId: String? = null
)

// Legacy format: location was a plain String before the maps feature
@Serializable
private data class DateProposalContentDtoLegacy(
    val dateTime: String,
    val location: String,
    val status: String,
    val previousProposalMessageId: String? = null
)

@Serializable
private data class LocationContentDtoMapper(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val address: String? = null
)

private val proposalJson = Json { ignoreUnknownKeys = true }

fun List<MessageWithSender>.toUiList(
    localUserId: String,
    reactionsMap: Map<String, List<ReactionSummary>> = emptyMap()
): List<MessageUi> {
    return this
        .sortedByDescending { it.message.createdAt }
        .groupBy {
            it.message.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .flatMap { (date, messages) ->
            messages.map { it.toUi(localUserId, reactionsMap) } + MessageUi.DateSeparator(
                id = date.toString(),
                date = DateUtils.formatDateSeparator(date)
            )
        }
}

fun MessageWithSender.toUi(
    localUserId: String,
    reactionsMap: Map<String, List<ReactionSummary>> = emptyMap()
): MessageUi {
    val isFromLocalUser = this.sender.userId == localUserId
    val reactions = reactionsMap[message.id] ?: emptyList()
    val dateProposal = if (message.messageType == MessageType.DATE_PROPOSAL) {
        parseDateProposal(message.id, message.content, isFromLocalUser)
    } else null

    val locationMessage = if (message.messageType == MessageType.LOCATION) {
        parseLocationMessage(message.content)
    } else null

    return if(isFromLocalUser) {
        MessageUi.LocalUserMessage(
            id = message.id,
            content = message.content,
            deliveryStatus = message.deliveryStatus,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            messageType = message.messageType,
            reactions = reactions,
            dateProposal = dateProposal,
            locationMessage = locationMessage
        )
    } else {
        MessageUi.OtherUserMessage(
            id = message.id,
            content = message.content,
            formattedSentTime = DateUtils.formatMessageTime(instant = message.createdAt),
            sender = sender.toUi(),
            messageType = message.messageType,
            reactions = reactions,
            dateProposal = dateProposal,
            locationMessage = locationMessage
        )
    }
}

private fun parseLocationMessage(content: String): LocationMessageUi? {
    return try {
        val dto = proposalJson.decodeFromString<LocationContentDtoMapper>(content)
        LocationMessageUi(
            latitude = dto.latitude,
            longitude = dto.longitude,
            name = dto.name,
            address = dto.address
        )
    } catch (_: Exception) {
        null
    }
}

private fun parseDateProposal(
    messageId: String,
    content: String,
    isFromLocalUser: Boolean
): DateProposalUi? {
    return try {
        val (dateTime, location, statusString) = try {
            val dto = proposalJson.decodeFromString<DateProposalContentDto>(content)
            Triple(
                dto.dateTime,
                DateProposalLocation(
                    name = dto.location.name,
                    address = dto.location.address,
                    latitude = dto.location.latitude,
                    longitude = dto.location.longitude,
                    placeId = dto.location.placeId
                ),
                dto.status
            )
        } catch (_: Exception) {
            // Backward compat: old messages stored location as a plain String
            val legacy = proposalJson.decodeFromString<DateProposalContentDtoLegacy>(content)
            Triple(
                legacy.dateTime,
                DateProposalLocation(
                    name = legacy.location,
                    address = legacy.location,
                    latitude = 0.0,
                    longitude = 0.0
                ),
                legacy.status
            )
        }
        val status = try {
            DateProposalStatus.valueOf(statusString)
        } catch (_: IllegalArgumentException) {
            DateProposalStatus.PENDING
        }
        val isPending = status == DateProposalStatus.PENDING
        DateProposalUi(
            messageId = messageId,
            dateTime = dateTime,
            location = location,
            status = status,
            canAccept = isPending && !isFromLocalUser,
            canReject = isPending && !isFromLocalUser,
            canCancel = isPending && isFromLocalUser,
            canEdit = isPending && isFromLocalUser
        )
    } catch (_: Exception) {
        null
    }
}
