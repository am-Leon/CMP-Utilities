package am.leon.utilities.common.presentation.viewModel.contract

interface IForm {
    val initialHash: Int?
    val trackedFields: Any?
    val isEmpty: Boolean get() = false
    val contentHash: Int get() = trackedFields.hashCode()
    val isModified: Boolean get() = contentHash != initialHash

    val isValidForAdd: Boolean get() = false
    val isValidForEdit: Boolean get() = isModified && isValidForAdd
}