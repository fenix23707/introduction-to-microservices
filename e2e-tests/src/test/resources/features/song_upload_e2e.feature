Feature: Song upload — end-to-end processing

  As an external API consumer
  I upload an MP3 file to resource-service and, once resource-processor has
  asynchronously extracted its metadata via Kafka, I can retrieve the song
  metadata from song-service.

  Background:
    Given the platform services are up and running

  Scenario: Uploading a valid MP3 file is asynchronously processed and its metadata becomes retrievable
    Given a valid MP3 file "track.mp3"
    When I upload the file to the resource service
    Then the upload should be accepted and a resource id returned
    And the song metadata should eventually be available with:
      | name     | Test Title  |
      | artist   | Test Artist |
      | album    | Test Album  |
      | year     | 2025        |
      | duration | 00:07       |

  Scenario: Uploading a non-MP3 file is rejected
    Given an invalid file "document.pdf" with content type "application/pdf"
    When I upload the file to the resource service
    Then the upload should be rejected with a client error
