package am.leon.utilities.helpers.fileSystem

import android.content.Context
import android.util.Base64
import android.webkit.MimeTypeMap
import am.leon.utilities.helpers.fileSystem.models.ContentEncoding
import am.leon.utilities.helpers.fileSystem.models.FileType
import am.leon.utilities.helpers.fileSystem.models.LeonFile
import am.leon.utilities.helpers.fileSystem.models.Path
import am.leon.utilities.helpers.fileSystem.models.PathComponent
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.atomic.AtomicReference

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

    private var context = AtomicReference<Context>(null)
    fun initialize(context: Context?) {
        this.context.set(context)
    }

    /**
     * Path to the common contents directory. Directory is guaranteed to exist (created if not exists already).
     *
     * On Android, it points to `files` directory.
     * On iOS, it points to `Documents` directory.
     */
    actual val contentsDirectory: Path
        get() {
            val path = context.get().filesDir.absolutePath
            File(path).mkdirs()
            return Path.simplified(path)
        }

    /**
     * Path to caches directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val cachesDirectory: Path
        get() = Path.simplified(context.get().cacheDir.absolutePath)

    /**
     * Path to Temporary directory. Directory is guaranteed to exist (created if not exists already).
     */
    actual val temporaryDirectory: Path
        get() = cachesDirectory

    private fun buildLeonFile(file: File): LeonFile {
        val fileType = when {
            file.isDirectory -> FileType.Directory
            file.isFile -> FileType.Regular
            else -> FileType.Unknown
        }

        val extension = file.path.substringAfterLast('.', "")
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        return LeonFile(
            name = file.name,
            absolutePath = PathComponent(file.absolutePath),
            canonicalPath = PathComponent(file.canonicalPath),
            createdAt = 0.0,
            modifiedAt = file.lastModified().toDouble(),
            size = file.length().toDouble(),
            bytes = file.readBytes(),
            type = fileType,
            mimeType = mimeType.orEmpty()
        )
    }

    /**
     * Returns a list of leonFile for the contents of directory at `path`.
     */
    actual fun readDir(path: String): List<LeonFile>? {
        val parent = File(path).canonicalFile ?: return null
        val fileList = parent.listFiles() ?: return null
        return fileList.map {
            buildLeonFile(it)
        }
    }

    /**
     * Returns a list of leonFile for the contents of directory at `pathComponent`.
     */
    actual fun readDir(pathComponent: PathComponent): List<LeonFile>? {
        val path = pathComponent.component ?: return null
        return readDir(path)
    }

    /**
     * Returns leonFile for the resource at [path].
     */
    actual fun leonFile(path: String): LeonFile? {
        val file = File(path)
        return buildLeonFile(file)
    }

    /**
     * Returns leonFile for the resource at `pathComponent`.
     */
    actual fun leonFile(pathComponent: PathComponent): LeonFile? {
        val path = pathComponent.component ?: return null
        return leonFile(path)
    }

    /**
     *
     * Returns the contents of the file located at `path`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(path: String, encoding: ContentEncoding): String? {
        val file = File(path).canonicalFile

        val charset = when (encoding) {
            ContentEncoding.Ascii -> Charsets.US_ASCII
            else -> Charsets.UTF_8
        }
        val reader = BufferedReader(InputStreamReader(FileInputStream(file), charset))
        val content = reader.readLines().joinToString("\n")

        if (encoding == ContentEncoding.Base64) {
            return String(Base64.decode(content, Base64.DEFAULT), Charsets.UTF_8)
        }
        return content
    }

    /**
     *
     * Returns the contents of the file located at `pathComponent`. The content is parsed according to `encoding`.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     *
     */
    actual fun readFile(pathComponent: PathComponent, encoding: ContentEncoding): String? {
        val path = pathComponent.component ?: return null
        return readFile(path, encoding)
    }

    /**
     * Returns the contents of the file located at `path` as ByteArray.
     */
    actual fun readFile(path: String): ByteArray? {
        val file = File(path).canonicalFile
        return file.readBytes()
    }

    /**
     * Returns the contents of the file located at `pathComponent` as ByteArray.
     */
    actual fun readFile(pathComponent: PathComponent): ByteArray? {
        val path = pathComponent.component ?: return null
        return readFile(path)
    }

    private fun writeFile(
        path: String, contents: String, create: Boolean, encoding: ContentEncoding,
        append: Boolean = false
    ): Boolean {
        val file = File(path).canonicalFile
        if (!file.exists()) {
            if (!create) return false
            file.createNewFile()
        }

        val finalContent: String
        val appendToFile: Boolean

        if (encoding == ContentEncoding.Base64) {
            val sourceString =
                if (append) (readFile(path, ContentEncoding.Base64) ?: "") + contents else contents

            finalContent =
                Base64.encodeToString(sourceString.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
            appendToFile = false

        } else {
            finalContent = contents
            appendToFile = append
        }

        val charset = when (encoding) {
            ContentEncoding.Ascii -> Charsets.US_ASCII
            else -> Charsets.UTF_8
        }
        val bufferedWriter =
            BufferedWriter(OutputStreamWriter(FileOutputStream(file, appendToFile), charset))
        bufferedWriter.write(finalContent)
        bufferedWriter.close()
        return true
    }

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        path: String, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean {
        return writeFile(path, contents, create, encoding, false)
    }

    /**
     * Writes `contents` to the file located at `pathComponent`. If `create` is true, then file is created if it does not exist.
     * For binary files, use `encoding` = [ContentEncoding.Base64].
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent, contents: String, create: Boolean, encoding: ContentEncoding
    ): Boolean {
        val path = pathComponent.component ?: return false
        return writeFile(path, contents, create, encoding, false)
    }

    /**
     * Writes `contents` to the file located at `path`. If `create` is true, then file is created if it does not exist.
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(path: String, contents: ByteArray, create: Boolean): Boolean {
        val file = File(path).canonicalFile
        if (!file.exists()) {
            if (!create) return false
            file.createNewFile()
        }

        FileOutputStream(file, false).write(contents)
        return true
    }

    /**
     * Writes `contents` to the file located at `pathComponent`. If `create` is true, then file is created if it does not exist.
     * * Returns true if operation is successful, otherwise false.
     */
    actual fun writeFile(
        pathComponent: PathComponent, contents: ByteArray, create: Boolean
    ): Boolean {
        val path = pathComponent.component ?: return false
        return writeFile(path, contents, create)
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
    ): Boolean {
        return writeFile(path, contents, create, encoding, true)
    }

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
    ): Boolean {
        val path = pathComponent.component ?: return false
        return writeFile(path, contents, create, encoding, true)
    }

    /**
     * Creates a file at `path` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(path: String): Boolean {
        val file = File(path).canonicalFile
        return file.createNewFile()
    }

    /**
     * Creates a file at `pathComponent` if it does not exist.
     * Returns false if file already exists, otherwise true.
     */
    actual fun touch(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false
        return touch(path)
    }

    /**
     * Creates a directory on `path`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(path: String, recursive: Boolean): Boolean {
        val file = File(path).canonicalFile
        return if (recursive) file.mkdirs() else file.mkdir()
    }

    /**
     * Creates a directory on `pathComponent`.
     * If `recursive` is true, then intermediate directories are also created.
     * Returns true if directory is created successfully.
     */
    actual fun mkdir(pathComponent: PathComponent, recursive: Boolean): Boolean {
        val path = pathComponent.component ?: return false
        return mkdir(path, recursive)
    }

    /**
     * Returns true if the file or directory exists at `path`.
     */
    actual fun exists(path: String): Boolean {
        return File(path).canonicalFile.exists()
    }

    /**
     * Returns true if the file or directory exists at `pathComponent`.
     */
    actual fun exists(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false
        return exists(path)
    }

    /**
     * Removes a file on `path`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun unlink(path: String): Boolean {
        return File(path).canonicalFile.deleteRecursively()
    }

    /**
     * Removes a file on `pathComponent`.
     * If it is a directory, its contents are removed as well.
     * Returns true if file is deleted successfully, otherwise false.
     */
    actual fun unlink(pathComponent: PathComponent): Boolean {
        val path = pathComponent.component ?: return false
        return unlink(path)
    }

    /**
     * Moves the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(srcPath: String, destPath: String): Boolean {
        val srcFile = File(srcPath).canonicalFile
        val destFile = File(destPath).canonicalFile
        return srcFile.renameTo(destFile)
    }

    /**
     * Moves the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are moved.
     * Returns true if the move is successful, otherwise false.
     */
    actual fun moveFile(
        srcPathComponent: PathComponent, destPathComponent: PathComponent
    ): Boolean {
        val srcPath = srcPathComponent.component ?: return false
        val destPath = destPathComponent.component ?: return false
        return moveFile(srcPath, destPath)
    }

    /**
     * Copies the file from `srcPath` to `destPath`.
     * If `srcPath` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(srcPath: String, destPath: String): Boolean {
        val srcFile = File(srcPath).canonicalFile
        val destFile = File(destPath).canonicalFile
        return srcFile.copyRecursively(destFile, true)
    }

    /**
     * Copies the file from `srcPathComponent` to `destPathComponent`.
     * If `srcPathComponent` is a directory, its contents including hidden files are copied.
     * Returns true if the copy is successful, otherwise false.
     */
    actual fun copyFile(
        srcPathComponent: PathComponent, destPathComponent: PathComponent
    ): Boolean {
        val srcPath = srcPathComponent.component ?: return false
        val destPath = destPathComponent.component ?: return false
        return copyFile(srcPath, destPath)
    }
}