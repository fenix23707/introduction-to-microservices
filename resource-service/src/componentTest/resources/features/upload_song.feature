Feature: Song upload

  Scenario: Uploading a valid MP3 file stores it and publishes an event
    Given a valid MP3 file "track.mp3"
    When I upload the file
    Then the file should be stored in S3
    And the resource metadata should be persisted in the database
    And a "SongUploaded" event should be published to Kafka

  Scenario: Uploading a non-MP3 file is rejected
    Given an invalid file "document.pdf" with type "application/pdf"
    When I upload the file
    Then the upload should be rejected with message "Only MP3 files are allowed"
    And nothing should be stored in S3, the database, or Kafka