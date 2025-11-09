package com.woowla.ghd.domain.entities

data class DeploymentWithRepo(
    val deployment: Deployment,
    val repoToCheck: RepoToCheck,
) : Comparable<DeploymentWithRepo> {
    companion object {
        val defaultComparator = compareByDescending<DeploymentWithRepo> { it.repoToCheck.groupName }.thenBy { it.deployment.createdAt }
    }

    val isSyncValid: Boolean = this.repoToCheck.areDeploymentsEnabled

    val groupKey: String by lazy {
        val joinPerRegex = !repoToCheck.deploymentRefNameRegex.isNullOrBlank()
        if (joinPerRegex) {
            val regex =  repoToCheck.deploymentRefNameRegex.toRegex()
            val matchResult = regex.find(deployment.refName ?: "")
            if (matchResult != null && !matchResult.groupValues.getOrNull(1).isNullOrBlank()) {
                matchResult.groupValues[1]
            } else {
                deployment.refName ?: "no-regex-match"
            }
        } else {
            repoToCheck.repository?.name ?: "no-repo-name"
        }
    }

    override fun compareTo(other: DeploymentWithRepo): Int {
        return defaultComparator.compare(this, other)
    }
}


/**
 * Group deployments per repo and then per group key (ref name regex or repo name).
 * Returns: List of (RepoToCheck paired with Map where Key=groupKey and Value=List<Deployment>)
 */
fun List<DeploymentWithRepo>.groupByGroupKeys(): List<Pair<RepoToCheck, Map<String, List<Deployment>>>> {
    return this
        .groupBy {
            // first group by repo to avoid mixing deployments with same group key from different repos
            it.repoToCheck
        }
        .map { (repoToCheck, deploymentsWithRepo) ->
            val deploymentsGroupedByKey = deploymentsWithRepo
                .filter {
                    it.deployment.refPrefix == "refs/tags/"
                }
                .groupBy {
                    it.groupKey
                }
                .mapValues { (_, deps) ->
                    deps
                        .map {
                            it.deployment
                        }
                        .sortedBy { deployment ->
                            repoToCheck.deploymentEnvironmentsList.indexOf(deployment.environment)
                        }
                }
            repoToCheck to deploymentsGroupedByKey
        }
}

/**
 * Return a list containing only the elements valid to store/show
 */
fun List<DeploymentWithRepo>.filterSyncValid(): List<DeploymentWithRepo> {
    return this.filter { deployment -> deployment.isSyncValid }
}

/**
 * Return a list containing only the elements which are not valid to store/show.
 */
fun List<DeploymentWithRepo>.filterNotSyncValid(): List<DeploymentWithRepo> {
    return this.filterNot { deployment -> deployment.isSyncValid }
}


/**
 * Filter deployments to keep only the latest (by created at) per ref tag and environment.
 */
fun List<DeploymentWithRepo>.filterDeployments(): List<DeploymentWithRepo> {
    val filteredDeploysGrouped = this
        // group all by group key
        .groupByGroupKeys()
        // filter latest per environment
        .flatMap { (repoToCheck, deploymentsPerGroupKey) ->
            deploymentsPerGroupKey.flatMap { (_, deployments) ->
                deployments
                    .groupBy { deployment ->
                        deployment.environment
                    }
                    .mapValues { (env, deploymentsPerEnv) ->
                        deploymentsPerEnv.maxByOrNull { it.createdAt }
                    }
                    .values
                    .filterNotNull()
                    .map { deployment ->
                        DeploymentWithRepo(
                            deployment = deployment,
                            repoToCheck = repoToCheck
                        )
                    }
            }
        }

    return filteredDeploysGrouped
}
