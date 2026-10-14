@file:OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)

package am.leon.utilities.helpers.fileSystem

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import am.leon.utilities.helpers.fileSystem.models.ContentEncoding
import am.leon.utilities.helpers.fileSystem.models.FileType
import am.leon.utilities.helpers.fileSystem.models.LeonFile
import am.leon.utilities.helpers.fileSystem.models.Path
import am.leon.utilities.helpers.fileSystem.models.PathComponent
import am.leon.utilities.helpers.fileSystem.models.toByteArray
import am.leon.utilities.helpers.fileSystem.models.toNSData
import platform.Foundation.NSASCIIStringEncoding
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDataBase64EncodingEndLineWithCarriageReturn
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileCreationDate
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSFileSize
import platform.Foundation.NSFileType
import platform.Foundation.NSFileTypeDirectory
import platform.Foundation.NSFileTypeRegular
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.base64EncodedDataWithOptions
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.fileHandleForUpdatingAtPath
import platform.Foundation.seekToEndOfFile
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.writeData
import platform.Foundation.writeToFile
import platform.UniformTypeIdentifiers.UTType
import kotlin.collections.get
import kotlin.collections.iterator

val String.nativeStr: NSString
    get() = NSString.create(string = this)

fun String.toData(encoding: ContentEncoding): NSData? {
    return when (encoding) {
        ContentEncoding.Base64 -> nativeStr.dataUsingEncoding(NSUTF8StringEncoding)
            ?.base64EncodedDataWithOptions(NSDataBase64EncodingEndLineWithCarriageReturn)

        ContentEncoding.Ascii -> nativeStr.dataUsingEncoding(NSASCIIStringEncoding)
        ContentEncoding.Utf8 -> nativeStr.dataUsingEncoding(NSUTF8StringEncoding)
    }
}

fun String.Companion.fromData(data: NSData, encoding: ContentEncoding): String? {
    return when (encoding) {
        ContentEncoding.Utf8 -> NSString.create(data, NSUTF8StringEncoding).toString()
        ContentEncoding.Ascii -> NSString.create(data, NSASCIIStringEncoding).toString()
        ContentEncoding.Base64 -> {
            val base64String =
                NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
                    ?: return null

            return base64String.split("\n")
                .filter { it.isNotEmpty() }
                .mapNotNull {
                    NSData.create(it, NSDataBase64EncodingEndLineWithCarriageReturn)
                }.mapNotNull {
                    String.fromData(it, ContentEncoding.Utf8)
                }.joinToString("\n")
        }
    }
}


/**
 * A thread safe singleton to access file system apis.
 *
 * ### Examples
 *
 * ```
 * val documentsDirectory = FileSystem.contentsDirectory
 *
 * val isFileCreated = FileSystem.touch(documentsDirectory.absolutePath?.byAppending("test.txt")!!)
 * ```
 */
actual object FileSystem {

    private var manager = NSFileManager.defaultManager

    private fun getDirUrl(directory: NSSearchPathDirectory, create: Boolean = false): NSURL? {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.URLForDirectory(
                directory,
                NSUserDomainMask,
                null,
                create,
                error.ptr
            )?.standardizedURL
        }
    }

    private fun getDirPath(directory: NSSearchPathDirectory, create: Boolean = false): Path {
        return Path.fromUrl(getDirUrl(directory, create))
    }

    private fun attributesOfFile(filePath: String): Map<String, Any?> {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val attributes =
                manager.attributesOfItemAtPath(filePath, error.ptr) ?: return emptyMap()
            val result = mutableMapOf<String, Any?>()
            for ((key, value) in attributes) {
                if (key != null && key is String) {
                    result[key] = value
                }
            }
            return result
        }
    }

    /**
     * Path to the common contents directory. Directory is guaranteed to exist (created if not exists already).
     *
     * On Android, it points to `files` directory.
     * On iOS, it points to `Documents` directory.
     */
    actual val contentsDirectory: Path
        get() = getDirPath(NSDocumentDirectory, true)

    /**
     * Path to caches directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val cachesDirectory: Path
        get() = getDirPath(NSCachesDirectory, true)

    /**
     * Path to Temporary directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val temporaryDirectory: Path
        get() = Path.fromUrlString(NSTemporaryDirectory())

    private fun fileExists(path: String, isDirectory: Boolean = false): Boolean {
        memScoped {
            val boolean = alloc<BooleanVar>()
            boolean.value = isDirectory
            return manager.fileExistsAtPath(path, isDirectory = boolean.ptr)
        }
    }

    private fun ensureFileExists(
        path: String, create: Boolean = false, isDirectory: Boolean = false
    ): Boolean {
        val exists = fileExists(path, isDirectory)
        if (!exists && !create) return false

        if (!exists && create) {
            return if (isDirectory) {
                manager.createDirectoryAtPath(path, emptyMap<Any?, Any>())
            } else {
                manager.createFileAtPath(path, null, emptyMap<Any?, Any>())
            }
        }
        return true
    }

    private fun readDir(url: NSURL?): List<LeonFile>? {
        val stdPath = url?.standardizedURL?.path ?: return null
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val contents =
                manager.contentsOfDirectoryAtPath(stdPath, error.ptr) ?: return emptyList()
            return contents.mapNotNull {
                buildLeonFile(url.URLByAppendingPathComponent(it as String) ?: return null)
            }
        }
    }

    /**
     * Returns a list of leonFile for the contents of directory at `path`.
     */
    actual fun readDir(path: String): List<LeonFile>? = readDir(Path.urlFromString(path))

    /**
     * Returns a list of leonFile for the contents of directory at `pathComponent`.
     */
    actual fun readDir(pathComponent: PathComponent): List<LeonFile>? = readDir(pathComponent.url)

    private fun buildLeonFile(url: NSURL?): LeonFile? {
        val stdPath = url?.standardizedURL?.path ?: return null
        val attributes = attributesOfFile(stdPath)
        val createdAt = toInterval(attributes[NSFileCreationDate])
        val modifiedAt = toInterval(attributes[NSFileModificationDate])
        val size = (attributes[NSFileSize] as? NSNumber)?.doubleValue
        val type = when (attributes[NSFileType] as? String) {
            NSFileTypeRegular -> FileType.Regular
            NSFileTypeDirectory -> FileType.Directory
            else -> FileType.Unknown
        }
        val mimeType = url.pathExtension?.let { UTType.typeWithFilenameExtension(it)?.preferredMIMEType }.orEmpty()

        return LeonFile(
            name = url.lastPathComponent ?: "",
            canonicalPath = PathComponent(stdPath),
            absolutePath = PathComponent(stdPath),
            createdAt = createdAt,
            modifiedAt = modifiedAt,
            size = size ?: Double.NaN,
            bytes = readFile(stdPath) ?: byteArrayOf(),
            type = type,
            mimeType = mimeType
        )
    }

    /**
     * Returns leonFile for the resource at [path].
     */
    actual fun leonFile(path: String): LeonFile? = buildLeonFile(Path.urlFromString(path))

    /**
     * Returns leonFile for the resource at `pathComponent`.
     */
    actual fun leonFile(pathComponent: PathComponent): LeonFile? = buildLeonFile(pathComponent.url)

    private fun readFile(url: NSURL?, encoding: ContentEncoding): String? {
        val path = url?.standardizedURL?.path ?: return null
        val data = manager.contentsAtPath(path) ?: return null
        return String.fromData(data, encoding)
    }

    /**
     *
     * Returns the contents of the file located at `path`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(path: String, encoding: ContentEncoding): String? =
        readFile(Path.urlFromString(path), encoding)

    /**
     *
     * Returns the contents of the file located at `pathComponent`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(pathComponent: PathComponent, encoding: ContentEncoding): String? =
        readFile(pathComponent.url, encoding)

    /**
     * Returns the contents of the file located at `path` as ByteArray.
     */
    actual fun readFile(path: String): ByteArray? {
        val url = Path.urlFromString(path)
        val pathFromUrl = url?.standardizedURL?.path ?: return null
        val data = manager.contentsAtPath(pathFromUrl) ?: return null
        return data.toByteArray()
    }

    /**
     * Returns the contents of the file located at `pathComponent` as ByteArray.
     */
    actual fun readFile(pathComponent: PathComponent): ByteArray? {
        val path = pathComponent.component ?: return null
        return readFile(path)
    }

    private fun toInterval(input: Any?): Double {
        if (input == null || input !is NSDate) return Double.NaN
        return input.timeIntervalSince1970()
    }

    private fun writeFile(
        url: NSURL?, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean {
        val path = url?.standardizedURL?.path ?: return false

        if (!ensureFileExists(path, create, false))
            return false

        val data = contents.toData(encoding) ?: return false
        return data.writeToFile(path, true)
    }

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        path: String, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean = writeFile(Path.urlFromString(path), contents, create, encoding)

    /**
     * Writes `contents` to the file located at `pathComponent`.
     * If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean = writeFile(pathComponent.url, contents, create, encoding)

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(path: String, contents: ByteArray, create: Boolean): Boolean {
        val pathStandardized = Path.urlFromString(path)?.standardizedURL?.path ?: return false

        if (!ensureFileExists(pathStandardized, create, false)) {
            return false
        }

        val data = contents.toNSData() ?: return false
        return data.writeToFile(pathStandardized, true)
    }

    /**
     * Writes `contents` to the file located at `pathComponent`. If `create` is true, then file is created if it does not exist.
     * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent, contents: ByteArray, create: Boolean
    ): Boolean {
        val path = pathComponent.component ?: return false
        return writeFile(path, contents, create)
    }

    private fun appendFile(
        url: NSURL?, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean {
        val path = url?.standardizedURL?.path ?: return false

        if (encoding == ContentEncoding.Base64) {
            val existingContents = readFile(url, ContentEncoding.Base64) ?: ""
            return writeFile(
                url,
                (existingContents + contents),
                create = create,
                encoding = ContentEncoding.Base64
            )
        }

        val data = contents.toData(encoding) ?: return false
        if (!ensureFileExists(path, create, false)) return false

        val handle = NSFileHandle.fileHandleForUpdatingAtPath(path) ?: return false
        handle.seekToEndOfFile()
        handle.writeData(data)
        handle.closeFile()
        return true
    }

    /**
     * Appends `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * The original contents are left unchanged.
     *
     * **If the `encoding` = [ContentEncoding.Base64], then existing contents are first read decoded into a UTF-8 string.
     * The new contents are appended and a new Base64 representation of the concatenated string is written to the file.**
     *
     * Returns true if operation is successful, otherwise false.
     */
    actual fun appendFile(
        path: String, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean = appendFile(Path.urlFromString(path), contents, create, encoding)

    /**
     * Appends `contents` to the file located at `pathComponent`.
     * If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * The original contents are left unchanged.
     *
     * **If the `encoding` = [ContentEncoding.Base64], then existing contents are first read decoded into a UTF-8 string.
     * The new contents are appended and a new Base64 representation of the concatenated string is written to the file.**
     *
     * Returns true if operation is successful, otherwise false.
     */
    actual fun appendFile(
        pathComponent: PathComponent, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean = appendFile(pathComponent.url, contents, create, encoding)

    private fun touch(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        if (fileExists(path, false)) return false
        return manager.createFileAtPath(path, null, null)
    }

    /**
     * Creates a file at `path` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(path: String): Boolean = touch(Path.urlFromString(path))

    /**
     * Creates a file at `pathComponent` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(pathComponent: PathComponent): Boolean = touch(pathComponent.url)

    private fun mkdir(url: NSURL?, recursive: Boolean): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        if (ensureFileExists(path, create = false, isDirectory = true)) return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.createDirectoryAtPath(path, recursive, emptyMap<Any?, Any>(), error.ptr)
        }
    }

    /**
     * Creates a directory on `path`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(path: String, recursive: Boolean): Boolean =
        mkdir(Path.urlFromString(path), recursive)

    /**
     * Creates a directory on `pathComponent`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(pathComponent: PathComponent, recursive: Boolean): Boolean =
        mkdir(pathComponent.url, recursive)

    private fun exists(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        return manager.fileExistsAtPath(path)
    }

    /**
     * Returns true if the file or directory exists at `path`.
     */
    actual fun exists(path: String): Boolean = exists(Path.urlFromString(path))

    /**
     * Returns true if the file or directory exists at `pathComponent`.
     */
    actual fun exists(pathComponent: PathComponent): Boolean = exists(pathComponent.url)

    private fun unlink(url: NSURL?): Boolean {
        val path = url?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.removeItemAtPath(path, error.ptr)
        }
    }

    /**
     * Removes a file on `path`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun unlink(path: String): Boolean = unlink(Path.urlFromString(path))

    /**
     * Removes a file on `pathComponent`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun unlink(pathComponent: PathComponent): Boolean = unlink(pathComponent.url)

    private fun moveFile(srcUrl: NSURL?, destUrl: NSURL?): Boolean {
        val srcPath = srcUrl?.standardizedURL?.path ?: return false
        val destPath = destUrl?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.moveItemAtPath(srcPath, destPath, error.ptr)
        }
    }

    /**
     * Moves the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(srcPath: String, destPath: String): Boolean =
        moveFile(Path.urlFromString(srcPath), Path.urlFromString(destPath))

    /**
     * Moves the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(
        srcPathComponent: PathComponent, destPathComponent: PathComponent
    ): Boolean = moveFile(srcPathComponent.url, destPathComponent.url)

    private fun copyFile(srcUrl: NSURL?, destUrl: NSURL?): Boolean {
        val srcPath = srcUrl?.standardizedURL?.path ?: return false
        val destPath = destUrl?.standardizedURL?.path ?: return false
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.copyItemAtPath(srcPath, destPath, error.ptr)
        }
    }

    /**
     * Copies the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(srcPath: String, destPath: String): Boolean =
        copyFile(Path.urlFromString(srcPath), Path.urlFromString(destPath))

    /**
     * Copies the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(
        srcPathComponent: PathComponent, destPathComponent: PathComponent
    ): Boolean = copyFile(srcPathComponent.url, destPathComponent.url)
}