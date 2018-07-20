#!/bin/bash

pg_dump -U caleb --format=plain --no-owner --no-acl ppg \
    | sed -E 's/(DROP|CREATE|COMMENT ON) EXTENSION/-- \1 EXTENSION/g' > backup.sql
