ALTER TABLE armies
    ADD bound_to BIGINT;

ALTER TABLE claimbuild_apps_built_by
    ADD built_claimbuilds_id BIGINT;

ALTER TABLE wars
    ADD initial_party BOOLEAN;

ALTER TABLE wars
    ADD joining_date TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE wars
    ADD participant_faction_id BIGINT;

CREATE UNIQUE INDEX IX_pk_production_claimbuild ON production_claimbuild (production_site_id, claimbuild_id);

ALTER TABLE armies
    ADD CONSTRAINT FK_ARMIES_BOUND_TO FOREIGN KEY (bound_to) REFERENCES rpchars (id);

ALTER TABLE wars
    ADD CONSTRAINT FK_WARS_ON_PARTICIPANT_FACTION FOREIGN KEY (participant_faction_id) REFERENCES factions (id);

ALTER TABLE claimbuild_apps_built_by
    ADD CONSTRAINT fk_claappbuiby_on_claimbuild_application FOREIGN KEY (built_claimbuilds_id) REFERENCES claimbuild_apps (id);

ALTER TABLE movement_path
    DROP CONSTRAINT fk7emisc3ivfq6qe9rxcql32b13;

ALTER TABLE claimbuild_application_production_sites
    DROP CONSTRAINT fk_claimbuild_application_production_sites_production_site_id;

ALTER TABLE war_defenders
    DROP CONSTRAINT fkiuo9k6nivhdwwr6q2ktfoxgjb;

ALTER TABLE war_aggressors
    DROP CONSTRAINT fkn288l28fyvw8jrmpvag32qd36;

ALTER TABLE user_roles
    DROP CONSTRAINT fkq0h6vpf3crn504yyep1hdv0vc;

ALTER TABLE claimbuild_apps_built_by
    DROP CONSTRAINT fkqbl9cikil8y0icpv9p75dk70;

DROP TABLE application_user CASCADE;

DROP TABLE sample_person CASCADE;

DROP TABLE user_roles CASCADE;

DROP TABLE war_participants CASCADE;

ALTER TABLE claimbuild_apps_built_by
    DROP COLUMN claimbuild_application_id;

ALTER TABLE production_sites
    ALTER COLUMN produced_resource TYPE VARCHAR USING (produced_resource::VARCHAR);

ALTER TABLE claimbuild_apps_built_by
    ADD CONSTRAINT pk_claimbuild_apps_builtby PRIMARY KEY (built_by_id, built_claimbuilds_id);