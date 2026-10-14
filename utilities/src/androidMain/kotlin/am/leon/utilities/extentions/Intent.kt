package am.leon.utilities.extentions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import am.leon.utilities.R

fun Context.whatsAppIntent(input: String) {
    val normalizedPhone = normalizeWhatsAppPhone(input)
    val packageName = "com.whatsapp"
    val url = "https://api.whatsapp.com/send?phone=$normalizedPhone"
    runCatching {
        val uri = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent(url)
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeWhatsAppPhone(input: String): String {
    val trimmed = input.trim()
    return runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            host.contains("wa.me") -> uri.path?.removePrefix("/")?.substringBefore("?")?.trim() ?: trimmed
            host.contains("whatsapp.com") -> uri.getQueryParameter("phone") ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)
}

fun Context.instagramIntent(input: String) {
    val normalizedProfile = normalizeInstagramIdentifier(input)
    val packageName = "com.instagram.android"
    val url = "https://www.instagram.com"
    runCatching {
        val uri = "$url/_u/$normalizedProfile".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent("$url/$normalizedProfile")
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeInstagramIdentifier(input: String): String {
    val trimmed = input.trim()
    return runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            host.contains("instagram.com") -> uri.pathSegments?.lastOrNull()?.takeIf { it.isNotBlank() } ?: trimmed
            trimmed.startsWith("instagram://") -> uri.getQueryParameter("username") ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)
}

fun Context.twitterIntent(input: String) {
    val normalizedProfile = normalizeTwitterIdentifier(input)
    val packageName = "com.twitter.android"
    runCatching {
        val uri = "twitter://user?screen_name=$normalizedProfile".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent("https://twitter.com/$normalizedProfile")
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeTwitterIdentifier(input: String): String {
    val trimmed = input.trim().removePrefix("@")
    return runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            host.contains("twitter.com") || host.contains("x.com") ->
                uri.pathSegments?.lastOrNull()?.takeIf { it.isNotBlank() }?.removePrefix("@") ?: trimmed

            trimmed.startsWith("twitter://") -> uri.getQueryParameter("screen_name") ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)
}

fun Context.webIntent(url: String) = runCatching {
    val fullPath = when {
        (url.startsWith("http://").not() && url.startsWith("https://").not()) -> "https://$url"
        else -> url
    }

    val intent = Intent(Intent.ACTION_VIEW, fullPath.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.phoneIntent(phone: String) = runCatching {
    val uri = "tel:$phone".toUri()
    val intent = Intent(Intent.ACTION_DIAL, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.gmailIntent(email: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, "mailto:$email".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent("https://www.gmail.com")
            else -> exception.printStackTrace()
        }
    }
}

fun Context.shareIntent(sharingURL: String) = runCatching {
    val sharingIntent = Intent("android.intent.action.SEND")
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share))
    sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingURL)
    sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)))
}

fun Context.mapIntent(latitude: Double, longitude: Double) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, "google.navigation:q=$latitude,$longitude".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
            else -> exception.printStackTrace()
        }
    }
}

fun Context.facebookIntent(pageId: String) {
    val normalizedPageId = normalizeFacebookIdentifier(pageId)
    val packageName = "com.facebook.katana"
    val url = "https://www.facebook.com/$normalizedPageId"
    runCatching {
        val uri = "fb://facewebmodal/f?href=$url".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent(url)
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeFacebookIdentifier(input: String): String {
    val trimmed = input.trim()
    return runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            (host.contains("facebook.com") || host.contains("fb.com")) && uri.path?.contains("profile.php") == true ->
                uri.getQueryParameter("id") ?: trimmed

            host.contains("facebook.com") || host.contains("fb.com") ->
                uri.pathSegments?.lastOrNull()?.takeIf { it.isNotBlank() && it != "profile.php" } ?: trimmed

            trimmed.startsWith("fb://") -> uri.path?.removePrefix("/profile/")?.trim() ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)
}

fun Context.snapchatIntent(input: String) {
    val normalizedUsername = normalizeSnapchatIdentifier(input)
    val packageName = "com.snapchat.android"
    val url = "https://www.snapchat.com/add/$normalizedUsername"
    runCatching {
        val uri: Uri = "snapchat://add/$normalizedUsername".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent(url)
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeSnapchatIdentifier(input: String): String {
    val trimmed = input.trim()
    val identifier = runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            host.contains("snapchat.com") -> uri.pathSegments?.lastOrNull()?.takeIf { it.isNotBlank() } ?: trimmed
            trimmed.startsWith("snapchat://") -> uri.path?.removePrefix("/add/@")?.removePrefix("/")?.trim() ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)

    return identifier.removePrefix("@")
}

fun Context.linkedInIntent(input: String) {
    val trimmed = input.trim()
    val packageName = "com.linkedin.android"
    val normalizedIdentifier = normalizeLinkedInIdentifier(trimmed)

    val isCompany = trimmed.contains("/company/") || trimmed.startsWith("company:")
    val profileType = if (isCompany) "company" else "in"
    val url = "https://www.linkedin.com/$profileType/$normalizedIdentifier"
    
    runCatching {
        val uri = "linkedin://$profileType/$normalizedIdentifier".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage(packageName)
        startActivity(intent)
    }.onFailure { exception ->
        when (exception) {
            is ActivityNotFoundException -> webIntent(url)
            else -> exception.printStackTrace()
        }
    }
}

private fun normalizeLinkedInIdentifier(input: String): String {
    val trimmed = input.trim()
    return runCatching {
        val uri = trimmed.toUri()
        val host = uri.host?.lowercase().orEmpty()

        when {
            host.contains("linkedin.com") -> extractFromLinkedInUrl(uri) ?: trimmed
            trimmed.startsWith("linkedin://") -> extractFromLinkedInScheme(uri) ?: trimmed
            else -> trimmed
        }
    }.getOrDefault(trimmed)
}

private fun extractFromLinkedInUrl(uri: Uri): String? {
    val pathSegments = uri.pathSegments.orEmpty()
    return when {
        pathSegments.contains("in") -> pathSegments.getOrNull(pathSegments.indexOf("in") + 1)
        pathSegments.contains("company") -> pathSegments.getOrNull(pathSegments.indexOf("company") + 1)
        else -> pathSegments.lastOrNull()
    }?.takeIf(String::isNotBlank)
}

private fun extractFromLinkedInScheme(uri: Uri): String? {
    return uri.path
        ?.removePrefix("/profile/")
        ?.removePrefix("/")
        ?.trim()
        ?.takeIf(String::isNotBlank)
}