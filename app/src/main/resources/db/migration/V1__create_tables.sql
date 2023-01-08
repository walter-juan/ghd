CREATE TABLE IF NOT EXISTS "PUBLIC"."DBSYNCSETTINGS"(
    "ID" UUID NOT NULL PRIMARY KEY,
    "githubPatToken" CHARACTER VARYING NOT NULL,
    "checkTimeout" BIGINT,
    "synchronizedAt" TIMESTAMP(9),
    "pullRequestCleanUpTimeout" BIGINT
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."DBREPOTOCHECK"(
    "ID" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1 RESTART WITH 8) NOT NULL PRIMARY KEY,
    "OWNER" CHARACTER VARYING NOT NULL,
    "NAME" CHARACTER VARYING NOT NULL,
    "pullNotificationsEnabled" BOOLEAN NOT NULL,
    "releaseNotificationsEnabled" BOOLEAN NOT NULL,
    "groupName" CHARACTER VARYING,
    "pullBranchRegex" CHARACTER VARYING
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."DBPULLREQUEST"(
    "ID" CHARACTER VARYING NOT NULL PRIMARY KEY,
    "NUMBER" BIGINT,
    "URL" CHARACTER VARYING,
    "STATE" CHARACTER VARYING,
    "TITLE" CHARACTER VARYING,
    "createdAt" TIMESTAMP(9),
    "updatedAt" TIMESTAMP(9),
    "mergedAt" TIMESTAMP(9),
    "DRAFT" BOOLEAN,
    "baseRef" CHARACTER VARYING,
    "headRef" CHARACTER VARYING,
    "authorLogin" CHARACTER VARYING,
    "authorUrl" CHARACTER VARYING,
    "authorAvatarUrl" CHARACTER VARYING,
    "appSeenAt" TIMESTAMP(9),
    "totalCommentsCount" BIGINT,
    "repoToCheckId" BIGINT NOT NULL REFERENCES DBREPOTOCHECK(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "PUBLIC"."DBRELEASE"(
    "ID" CHARACTER VARYING NOT NULL PRIMARY KEY,
    "NAME" CHARACTER VARYING,
    "tagName" CHARACTER VARYING,
    "URL" CHARACTER VARYING,
    "publishedAt" TIMESTAMP(9),
    "authorLogin" CHARACTER VARYING,
    "authorUrl" CHARACTER VARYING,
    "authorAvatarUrl" CHARACTER VARYING,
    "repoToCheckId" BIGINT NOT NULL REFERENCES DBREPOTOCHECK(ID) ON DELETE CASCADE
);
