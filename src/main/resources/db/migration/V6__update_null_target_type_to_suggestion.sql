UPDATE notification
SET target_type = 'SUGGESTION'
WHERE event_type = 'SUGGESTION_ANSWERED' AND target_type IS NULL;