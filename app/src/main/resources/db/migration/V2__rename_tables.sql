 ALTER TABLE "PUBLIC"."DBSYNCSETTINGS" RENAME TO sync_setting;
 ALTER TABLE "PUBLIC"."DBPULLREQUEST" RENAME TO pull_request;
 ALTER TABLE "PUBLIC"."DBRELEASE" RENAME TO release;
 ALTER TABLE "PUBLIC"."DBREPOTOCHECK" RENAME TO repo_to_check;

 ALTER TABLE sync_setting ALTER COLUMN "ID" RENAME TO id;
 ALTER TABLE sync_setting ALTER COLUMN "githubPatToken" RENAME TO github_pat_token;
 ALTER TABLE sync_setting ALTER COLUMN "checkTimeout" RENAME TO check_timeout;
 ALTER TABLE sync_setting ALTER COLUMN "synchronizedAt" RENAME TO synchronized_at;
 ALTER TABLE sync_setting ALTER COLUMN "pullRequestCleanUpTimeout" RENAME TO pull_request_clean_up_timeout;

 ALTER TABLE pull_request ALTER COLUMN "ID" RENAME TO id;
 ALTER TABLE pull_request ALTER COLUMN "NUMBER" RENAME TO number;
 ALTER TABLE pull_request ALTER COLUMN "URL" RENAME TO url;
 ALTER TABLE pull_request ALTER COLUMN "STATE" RENAME TO state;
 ALTER TABLE pull_request ALTER COLUMN "TITLE" RENAME TO title;
 ALTER TABLE pull_request ALTER COLUMN "createdAt" RENAME TO created_at;
 ALTER TABLE pull_request ALTER COLUMN "updatedAt" RENAME TO updated_at;
 ALTER TABLE pull_request ALTER COLUMN "mergedAt" RENAME TO merged_at;
 ALTER TABLE pull_request ALTER COLUMN "DRAFT" RENAME TO draft;
 ALTER TABLE pull_request ALTER COLUMN "baseRef" RENAME TO base_ref;
 ALTER TABLE pull_request ALTER COLUMN "headRef" RENAME TO head_ref;
 ALTER TABLE pull_request ALTER COLUMN "authorLogin" RENAME TO author_login;
 ALTER TABLE pull_request ALTER COLUMN "authorUrl" RENAME TO author_url;
 ALTER TABLE pull_request ALTER COLUMN "authorAvatarUrl" RENAME TO author_avatar_url;
 ALTER TABLE pull_request ALTER COLUMN "appSeenAt" RENAME TO app_seen_at;
 ALTER TABLE pull_request ALTER COLUMN "totalCommentsCount" RENAME TO total_comments_count;
 ALTER TABLE pull_request ALTER COLUMN "repoToCheckId" RENAME TO repo_to_check_id;

 ALTER TABLE release ALTER COLUMN "ID" RENAME TO id;
 ALTER TABLE release ALTER COLUMN "NAME" RENAME TO name;
 ALTER TABLE release ALTER COLUMN "tagName" RENAME TO tag_name;
 ALTER TABLE release ALTER COLUMN "URL" RENAME TO url;
 ALTER TABLE release ALTER COLUMN "publishedAt" RENAME TO published_at;
 ALTER TABLE release ALTER COLUMN "authorLogin" RENAME TO author_login;
 ALTER TABLE release ALTER COLUMN "authorUrl" RENAME TO author_url;
 ALTER TABLE release ALTER COLUMN "authorAvatarUrl" RENAME TO author_avatar_url;
 ALTER TABLE release ALTER COLUMN "repoToCheckId" RENAME TO repo_to_check_id;

 ALTER TABLE repo_to_check ALTER COLUMN "ID" RENAME TO id;
 ALTER TABLE repo_to_check ALTER COLUMN "OWNER" RENAME TO owner;
 ALTER TABLE repo_to_check ALTER COLUMN "NAME" RENAME TO name;
 ALTER TABLE repo_to_check ALTER COLUMN "pullNotificationsEnabled" RENAME TO pull_notifications_enabled;
 ALTER TABLE repo_to_check ALTER COLUMN "releaseNotificationsEnabled" RENAME TO release_notifications_enabled;
 ALTER TABLE repo_to_check ALTER COLUMN "groupName" RENAME TO group_name;
 ALTER TABLE repo_to_check ALTER COLUMN "pullBranchRegex" RENAME TO pull_branch_regex;