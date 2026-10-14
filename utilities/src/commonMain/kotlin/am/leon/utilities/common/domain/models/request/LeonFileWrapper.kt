package am.leon.utilities.common.domain.models.request

import am.leon.utilities.common.domain.models.domain.IInitialState
import am.leon.utilities.helpers.fileSystem.models.LeonFile

sealed class LeonFileWrapper : IInitialState {
    data class Single(val file: LeonFile) : LeonFileWrapper() {
        override fun isInitialState(): Boolean =
            file.bytes.isEmpty() or file.absolutePath.component.isNullOrEmpty() or
                    file.canonicalPath.component.isNullOrEmpty()
    }

    data class Multiple(val files: List<LeonFile>) : LeonFileWrapper() {
        override fun isInitialState(): Boolean = files.isEmpty()
    }
}