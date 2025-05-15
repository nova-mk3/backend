CREATE TABLE notification (
                              id BINARY(16) NOT NULL PRIMARY KEY,
                              receiver_id BINARY(16) NOT NULL,
                              message VARCHAR(255) NOT NULL,
                              event_type VARCHAR(50) NOT NULL,
                              target_id BINARY(16) NOT NULL,
                              target_type VARCHAR(50),
                              is_read BOOLEAN NOT NULL,
                              created_time DATETIME(6) NOT NULL,

                              CONSTRAINT fk_notification_receiver FOREIGN KEY (receiver_id)
                                  REFERENCES member (member_id)
                                  ON DELETE CASCADE
);