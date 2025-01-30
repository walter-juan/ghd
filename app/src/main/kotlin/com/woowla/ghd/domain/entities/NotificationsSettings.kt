package com.woowla.ghd.domain.entities

import arrow.optics.optics

@optics data class NotificationsSettings(
    // filters
    val filterUsername: String,

    // pull requests state
    val stateEnabledOption: EnabledOption,
    var stateOpenFromOthersPullRequestsEnabled: Boolean,
    var stateClosedFromOthersPullRequestsEnabled: Boolean,
    var stateMergedFromOthersPullRequestsEnabled: Boolean,
    var stateDraftFromOthersPullRequestsEnabled: Boolean,

    // pull requests activity
    val activityEnabledOption: EnabledOption,
    var activityReviewsFromYourPullRequestsEnabled: Boolean,
    var activityReviewsReRequestEnabled: Boolean,
    var activityChecksFromYourPullRequestsEnabled: Boolean,
    var activityMergeableFromYourPullRequestsEnabled: Boolean,

    // releases
    val newReleaseEnabled: Boolean,
) {
    companion object {
        val defaultEnabledOption = EnabledOption.NONE
    }

    enum class EnabledOption { NONE, ALL, FILTERED }

    val validStateEnabledOption: EnabledOption = if (isEnabledOptionAvailable(
            stateEnabledOption
        )
    ) {
        stateEnabledOption
    } else {
        defaultEnabledOption
    }
    val validActivityEnabledOption: EnabledOption = if (isEnabledOptionAvailable(
            activityEnabledOption
        )
    ) {
        activityEnabledOption
    } else {
        defaultEnabledOption
    }

    fun isEnabledOptionAvailable(option: EnabledOption): Boolean {
        return if (option == EnabledOption.FILTERED) {
            filterUsername.isNotBlank()
        } else {
            true
        }
    }
}
