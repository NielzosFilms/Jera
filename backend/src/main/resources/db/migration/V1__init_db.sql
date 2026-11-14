CREATE TABLE activity_events
(
    id          UUID PRIMARY KEY,
    user_id     UUID        NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    type        VARCHAR(20) NOT NULL,
    payload     JSONB       NOT NULL,
    source      VARCHAR(20) NOT NULL,
    status      VARCHAR(10) NOT NULL
);

CREATE TABLE skills
(
    id           UUID PRIMARY KEY,
    skill        varchar(50) NOT NULL,
    parent_skill UUID NULL
);

CREATE TABLE xp_rules
(
    activity_type VARCHAR(20) NOT NULL,
    skill_id      UUID        NOT NULL REFERENCES skills,
    shape         VARCHAR(20) NOT NULL,
    shape_knobs   JSONB       NOT NULL,
    PRIMARY KEY (activity_type, skill_id)
);

CREATE TABLE skill_progress
(
    skill_id UUID NOT NULL REFERENCES skills (id),
    user_id  UUID NOT NULL,
    xp       INT  NOT NULL,
    level    INT  NOT NULL,
    PRIMARY KEY (skill_id, user_id)
);

CREATE TABLE attributes
(
    attribute VARCHAR(5) PRIMARY KEY
);

CREATE TABLE skill_attributes
(
    skill_id  UUID             NOT NULL REFERENCES skills (id),
    attribute VARCHAR(5)       NOT NULL REFERENCES attributes (attribute),
    weight    DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (skill_id, attribute)
);
