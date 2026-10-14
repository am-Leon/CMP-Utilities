package am.leon.utilities.helpers.json.parsing

import kotlinx.serialization.json.JsonElement

interface IJsonParsingStrategy<Model> {
    fun parse(json: JsonElement): Model
}