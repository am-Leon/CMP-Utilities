package am.leon.utilities.common.data.mapper

/**
 * Converts one feature's model between the layers it lives in.
 *
 * Only [dtoToDomain] is abstract: every feature reads from the network, while the rest throw
 * until overridden, so a feature that never writes or never caches implements nothing extra.
 * Pass [Unit] for [Entity] when the feature has no local table.
 *
 * The list overloads are final - they map element by element through the single-model ones.
 */
abstract class Mapper<Dto, Domain, Entity> {

    abstract fun dtoToDomain(model: Dto): Domain

    open fun domainToDto(model: Domain): Dto =
        throw NotImplementedError("override and implement this method")

    fun dtoToDomain(list: List<Dto>?): List<Domain> = (list ?: emptyList()).map(::dtoToDomain)

    fun domainToDto(list: List<Domain>?): List<Dto> = (list ?: emptyList()).map(::domainToDto)

    open fun entityToDomain(model: Entity): Domain =
        throw NotImplementedError("override and implement this method")

    open fun domainToEntity(model: Domain): Entity =
        throw NotImplementedError("override and implement this method")

    fun entityToDomain(list: List<Entity>): List<Domain> = list.map(::entityToDomain)

    fun domainToEntity(list: List<Domain>): List<Entity> = list.map(::domainToEntity)

    open fun entityToDto(model: Entity): Dto =
        throw NotImplementedError("override and implement this method")

    open fun dtoToEntity(model: Dto): Entity =
        throw NotImplementedError("override and implement this method")

    fun entityToDto(list: List<Entity>): List<Dto> = list.map(::entityToDto)

    fun dtoToEntity(list: List<Dto>): List<Entity> = list.map(::dtoToEntity)
}