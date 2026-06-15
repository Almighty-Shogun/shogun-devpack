package ms.shogun.devpack.codeShare.uploaders

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

import ms.shogun.devpack.ShogunBundle.message
import ms.shogun.devpack.codeShare.JsonEscaper
import ms.shogun.devpack.codeShare.CodeShareResult
import ms.shogun.devpack.codeShare.CodeShareRequest
import ms.shogun.devpack.codeShare.CodeShareUploader
import ms.shogun.devpack.codeShare.CodeShareException
import ms.shogun.devpack.codeShare.github.GitHubTokenResolver

/**
 * Code sharing uploader that creates GitHub Gists.
 *
 * @param token GitHub token with permission to create gists.
 * @param publicGist Whether newly created gists should be public.
 *
 * @author Almighty-Shogun
 * @since 1.0.0
 */
class GitHubGistUploader(private val token: String?, private val publicGist: Boolean) : CodeShareUploader {
    private val httpClient = HttpClient.newHttpClient()

    override fun upload(request: CodeShareRequest): CodeShareResult {
        val uploadToken = GitHubTokenResolver(token).resolve()
            ?: throw CodeShareException(message("code-share.github.error.missing-token"))

        val response = httpClient.send(gistRequest(request, uploadToken), HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            throw CodeShareException(
                message(
                    "code-share.github.error.upload-failed",
                    response.statusCode()
                )
            )
        }

        return CodeShareResult(extractHtmlUrl(response.body()))
    }

    /**
     * Builds the GitHub API request.
     *
     * @param request Code share request.
     * @param token GitHub bearer token.
     *
     * @return HTTP request for creating a gist.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun gistRequest(request: CodeShareRequest, token: String): HttpRequest =
        HttpRequest.newBuilder(URI.create("https://api.github.com/gists"))
            .header("Accept", "application/vnd.github+json")
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .POST(HttpRequest.BodyPublishers.ofString(gistPayload(request)))
            .build()

    /**
     * Builds the JSON payload sent to GitHub.
     *
     * @param request Code share request.
     *
     * @return JSON payload.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun gistPayload(request: CodeShareRequest): String {
        val content = JsonEscaper.escape(request.content)
        val fileName = JsonEscaper.escape(request.fileName)

        return """
            {
              "description": "Uploaded from Shogun's DevPack",
              "public": $publicGist,
              "files": {
                "$fileName": {
                  "content": "$content"
                }
              }
            }
        """.trimIndent()
    }

    /**
     * Extracts the gist browser URL from GitHub's JSON response.
     *
     * @param responseBody GitHub response JSON.
     *
     * @return Gist browser URL.
     *
     * @throws CodeShareException when the response does not contain a URL.
     *
     * @author Almighty-Shogun
     * @since 1.0.0
     */
    private fun extractHtmlUrl(responseBody: String): String {
        val match = HTML_URL_REGEX.find(responseBody)

        return match?.groupValues?.get(1)
            ?: throw CodeShareException(message("code-share.github.error.missing-url"))
    }

    companion object {
        private val HTML_URL_REGEX = Regex(""""html_url"\s*:\s*"([^"]+)"""")
    }
}
