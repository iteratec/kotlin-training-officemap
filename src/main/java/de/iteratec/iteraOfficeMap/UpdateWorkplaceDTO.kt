package de.iteratec.iteraOfficeMap

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateWorkplaceDTO(

        @JsonProperty(required = true)
        val id: Long,

        @JsonProperty(required = true)
        val x: Int,

        @JsonProperty(required = true)
        val y: Int,

        @JsonProperty(required = true)
        val name: String,

        @JsonProperty(required = true)
        val mapId: String,

        @JsonProperty(required = true)
        val equipment: String

)
