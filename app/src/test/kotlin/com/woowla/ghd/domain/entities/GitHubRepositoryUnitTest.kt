package com.woowla.ghd.domain.entities

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GitHubRepositoryUnitTest : StringSpec({
    val validUrlList = """
            https://github.com/user/project.git
            http://github.com/user/project.git
            https://github.com/user/project.git/
            http://github.com/user/project.git/
            https://github.com/user/project
            http://github.com/user/project
            https://github.com/user/project/
            http://github.com/user/project/
            https://www.github.com/user/project.git
            http://www.github.com/user/project.git
            https://www.github.com/user/project.git/
            http://www.github.com/user/project.git/
            https://www.github.com/user/project
            http://www.github.com/user/project
            https://www.github.com/user/project/
            http://www.github.com/user/project/
        """.trimIndent().split("\n")
    val invalidUrlList = """
            git@github.com:user/project.git
            git@192.168.101.127:user/project.git
            https://192.168.101.127/user/project.git
            http://192.168.101.127/user/project.git
            ssh://user@host.xz:port/path/to/repo.git/
            ssh://user@host.xz/path/to/repo.git/
            ssh://host.xz:port/path/to/repo.git/
            ssh://host.xz/path/to/repo.git/
            ssh://user@host.xz/path/to/repo.git/
            ssh://host.xz/path/to/repo.git/
            ssh://user@host.xz/~user/path/to/repo.git/
            ssh://host.xz/~user/path/to/repo.git/
            ssh://user@host.xz/~/path/to/repo.git
            ssh://host.xz/~/path/to/repo.git
            git://host.xz/path/to/repo.git/
            git://host.xz/~user/path/to/repo.git/
            http://host.xz/path/to/repo.git/
            https://host.xz/path/to/repo.git/
            /path/to/repo.git/
            path/to/repo.git/
            ~/path/to/repo.git
            file:///path/to/repo.git/
            file://~/path/to/repo.git/
            user@host.xz:/path/to/repo.git/
            host.xz:/path/to/repo.git/
            user@host.xz:~user/path/to/repo.git/
            host.xz:~user/path/to/repo.git/
            user@host.xz:path/to/repo.git
            host.xz:path/to/repo.git
            rsync://host.xz/path/to/repo.git/
        """.trimIndent().split("\n")

    validUrlList.forEach { url ->
        "fromUrl should return a GitHubRepository object with valid URL: $url" {
            val repo = Repository.fromUrl(url)
            repo.owner shouldBe "user"
            repo.name shouldBe "project"
        }
    }

    invalidUrlList.forEach { url ->
        "fromUrl should throw an exception with invalid URL: $url" {
            shouldThrow<IllegalArgumentException> {
                Repository.fromUrl(url)
            }
        }
    }
})