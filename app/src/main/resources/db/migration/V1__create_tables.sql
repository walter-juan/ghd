CREATE TABLE IF NOT EXISTS "PUBLIC"."REPO_TO_CHECK"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "OWNER" CHARACTER VARYING NOT NULL,
    "NAME" CHARACTER VARYING NOT NULL,
    "GROUP_NAME" CHARACTER VARYING,
    "PULL_BRANCH_REGEX" CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."PULL_REQUEST"(
    "ID" CHARACTER VARYING NOT NULL,
    "NUMBER" BIGINT NOT NULL,
    "URL" CHARACTER VARYING NOT NULL,
    "STATE" CHARACTER VARYING,
    "TITLE" CHARACTER VARYING,
    "CREATED_AT" TIMESTAMP(9) NOT NULL,
    "UPDATED_AT" TIMESTAMP(9) NOT NULL,
    "MERGED_AT" TIMESTAMP(9),
    "IS_DRAFT" BOOLEAN NOT NULL,
    "BASE_REF" CHARACTER VARYING,
    "HEAD_REF" CHARACTER VARYING,
    "AUTHOR_LOGIN" CHARACTER VARYING,
    "AUTHOR_URL" CHARACTER VARYING,
    "AUTHOR_AVATAR_URL" CHARACTER VARYING,
    "APP_SEEN_AT" TIMESTAMP(9),
    "TOTAL_COMMENTS_COUNT" BIGINT,
    "MERGEABLE" CHARACTER VARYING,
    "LAST_COMMIT_CHECK_ROLLUP_STATUS" CHARACTER VARYING,
    "REPO_TO_CHECK_ID" BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."RELEASE"(
    "ID" CHARACTER VARYING NOT NULL,
    "NAME" CHARACTER VARYING,
    "TAG_NAME" CHARACTER VARYING NOT NULL,
    "URL" CHARACTER VARYING NOT NULL,
    "PUBLISHED_AT" TIMESTAMP(9),
    "AUTHOR_LOGIN" CHARACTER VARYING,
    "AUTHOR_URL" CHARACTER VARYING,
    "AUTHOR_AVATAR_URL" CHARACTER VARYING,
    "REPO_TO_CHECK_ID" BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."REVIEW"(
    "ID" CHARACTER VARYING NOT NULL,
    "SUBMITTED_AT" TIMESTAMP(9),
    "URL" CHARACTER VARYING NOT NULL,
    "STATE" CHARACTER VARYING,
    "AUTHOR_LOGIN" CHARACTER VARYING,
    "AUTHOR_URL" CHARACTER VARYING,
    "AUTHOR_AVATAR_URL" CHARACTER VARYING,
    "PULL_REQUEST_ID" CHARACTER VARYING NOT NULL
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."SYNC_SETTING"(
    "ID" UUID NOT NULL,
    "GITHUB_PAT_TOKEN" CHARACTER VARYING NOT NULL,
    "CHECK_TIMEOUT" BIGINT,
    "PULL_REQUEST_CLEAN_UP_TIMEOUT" BIGINT
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."SYNC_RESULT"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "START_AT" TIMESTAMP(9) NOT NULL,
    "END_AT" TIMESTAMP(9)
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."SYNC_RESULT_ENTRY"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
    "IS_SUCCESS" BOOLEAN NOT NULL,
    "START_AT" TIMESTAMP(9) NOT NULL,
    "END_AT" TIMESTAMP(9) NOT NULL,
    "ORIGIN" CHARACTER VARYING NOT NULL,
    "ERROR" CHARACTER VARYING,
    "ERROR_MESSAGE" CHARACTER VARYING,
    "SYNC_RESULT_ID" BIGINT NOT NULL,
    "REPO_TO_CHECK_ID" BIGINT
);

ALTER TABLE "PUBLIC"."REPO_TO_CHECK" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_7" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."PULL_REQUEST" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_5" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."RELEASE" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_6" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."REVIEW" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_8" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."SYNC_SETTING" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_A" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."SYNC_RESULT" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_C" PRIMARY KEY("ID");
ALTER TABLE "PUBLIC"."SYNC_RESULT_ENTRY" ADD CONSTRAINT "PUBLIC"."CONSTRAINT_72" PRIMARY KEY("ID");

ALTER TABLE "PUBLIC"."PULL_REQUEST" ADD CONSTRAINT "PUBLIC"."FK_PULL_REQUEST_REPO_TO_CHECK_ID__ID" FOREIGN KEY("REPO_TO_CHECK_ID") REFERENCES "PUBLIC"."REPO_TO_CHECK"("ID") ON DELETE CASCADE NOCHECK;
ALTER TABLE "PUBLIC"."RELEASE" ADD CONSTRAINT "PUBLIC"."FK_RELEASE_REPO_TO_CHECK_ID__ID" FOREIGN KEY("REPO_TO_CHECK_ID") REFERENCES "PUBLIC"."REPO_TO_CHECK"("ID") ON DELETE CASCADE NOCHECK;
ALTER TABLE "PUBLIC"."REVIEW" ADD CONSTRAINT "PUBLIC"."FK_REVIEW_PULL_REQUEST_ID__ID" FOREIGN KEY("PULL_REQUEST_ID") REFERENCES "PUBLIC"."PULL_REQUEST"("ID") ON DELETE CASCADE NOCHECK;
ALTER TABLE "PUBLIC"."SYNC_RESULT_ENTRY" ADD CONSTRAINT "PUBLIC"."FK_SYNC_RESULT_ENTRY_SYNC_RESULT_ID__ID" FOREIGN KEY("SYNC_RESULT_ID") REFERENCES "PUBLIC"."SYNC_RESULT"("ID") ON DELETE CASCADE NOCHECK;
ALTER TABLE "PUBLIC"."SYNC_RESULT_ENTRY" ADD CONSTRAINT "PUBLIC"."FK_SYNC_RESULT_ENTRY_REPO_TO_CHECK_ID__ID" FOREIGN KEY("REPO_TO_CHECK_ID") REFERENCES "PUBLIC"."REPO_TO_CHECK"("ID") ON DELETE CASCADE NOCHECK;
