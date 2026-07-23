package com.epam.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.epam.common.config.property.CsvStringProperties;
import com.epam.common.exception.CsvIdsLengthExceededException;
import com.epam.common.exception.InvalidIdParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IdsAsCsvParserTest {

    private IdsAsCsvParser idsAsCsvParser;
    private CsvStringProperties csvStringProperties;

    @BeforeEach
    void setUp() {
        csvStringProperties = mock(CsvStringProperties.class);
        when(csvStringProperties.delimiter()).thenReturn(",");
        when(csvStringProperties.maxLength()).thenReturn(100L);
        idsAsCsvParser = new IdsAsCsvParser(csvStringProperties);
    }

    @Test
    void shouldReturnSingleIdWhenParsingValidSingleId() {
        String rawIds = "123";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .isNotNull()
            .hasSize(1)
            .contains(123L);
    }

    @Test
    void shouldReturnMultipleIdsWhenParsingValidMultipleIds() {
        String rawIds = "123,456,789";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .isNotNull()
            .hasSize(3)
            .contains(123L, 456L, 789L)
            .containsExactly(123L, 456L, 789L);
    }

    @Test
    void shouldTrimWhitespaceWhenParsingIdsWithWhitespace() {
        String rawIds = " 123 , 456 , 789 ";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .isNotNull()
            .hasSize(3)
            .containsExactly(123L, 456L, 789L);
    }

    @Test
    void shouldThrowInvalidIdParseExceptionWhenParsingBlankString() {
        String rawIds = "";

        assertThatThrownBy(() -> idsAsCsvParser.parseRawIdsString(rawIds))
            .isInstanceOf(InvalidIdParseException.class);
    }

    @Test
    void shouldThrowInvalidIdParseExceptionWhenParsingNullString() {
        assertThatThrownBy(() -> idsAsCsvParser.parseRawIdsString(null))
            .isInstanceOf(InvalidIdParseException.class);
    }

    @Test
    void shouldThrowInvalidIdParseExceptionWhenParsingWhitespaceOnlyString() {
        String rawIds = "   ";

        assertThatThrownBy(() -> idsAsCsvParser.parseRawIdsString(rawIds))
            .isInstanceOf(InvalidIdParseException.class);
    }

    @Test
    void shouldThrowInvalidIdParseExceptionWhenParsingNonNumericId() {
        String rawIds = "123,abc,789";

        assertThatThrownBy(() -> idsAsCsvParser.parseRawIdsString(rawIds))
            .isInstanceOf(InvalidIdParseException.class);
    }

    @Test
    void shouldThrowCsvIdsLengthExceededExceptionWhenStringExceedsMaxLength() {
        String rawIds = "1".repeat(101);

        assertThatThrownBy(() -> idsAsCsvParser.parseRawIdsString(rawIds))
            .isInstanceOf(CsvIdsLengthExceededException.class);
    }

    @Test
    void shouldReturnParsedIdsWhenStringAtMaxLength() {
        String rawIds = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldFilterEmptyElementsWhenParsingIdsWithEmptyElements() {
        String rawIds = "123,,456";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .hasSize(2)
            .containsExactly(123L, 456L);
    }

    @Test
    void shouldParseLargeNumbersWhenParsingValidLargeIds() {
        String rawIds = "9223372036854775807,1000000000000000000";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .hasSize(2)
            .contains(9223372036854775807L, 1000000000000000000L);
    }

    @Test
    void shouldParseZeroWhenParsingIdIncludingZero() {
        String rawIds = "0,123";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .hasSize(2)
            .contains(0L, 123L);
    }

    @Test
    void shouldParseNegativeNumbersWhenParsingIdWithNegativeValues() {
        String rawIds = "-123,456,-789";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .hasSize(3)
            .contains(-123L, 456L, -789L);
    }

    @Test
    void shouldReturnSingleIdStringWhenConvertingSingleId() {
        List<Long> ids = List.of(123L);
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isNotNull()
            .isEqualTo("123");
    }

    @Test
    void shouldReturnCommaSeparatedStringWhenConvertingMultipleIds() {
        List<Long> ids = List.of(123L, 456L, 789L);
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isNotNull()
            .isEqualTo("123,456,789");
    }

    @Test
    void shouldReturnEmptyStringWhenConvertingEmptyList() {
        List<Long> ids = List.of();
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Test
    void shouldConvertZeroWhenConvertingSingleZeroId() {
        List<Long> ids = List.of(0L);
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isEqualTo("0");
    }

    @Test
    void shouldConvertNegativeNumbersWhenConvertingNegativeIds() {
        List<Long> ids = List.of(-123L, 456L, -789L);
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isEqualTo("-123,456,-789");
    }

    @Test
    void shouldRoundTripSuccessfullyWhenParsingAndConvertingBack() {
        String originalRawIds = "123,456,789";
        List<Long> parsed = idsAsCsvParser.parseRawIdsString(originalRawIds);
        String converted = idsAsCsvParser.toRawIdsString(parsed);

        assertThat(converted)
            .isEqualTo(originalRawIds);
    }

    @Test
    void shouldNormalizeWhitespaceWhenRoundTripConvertingWithWhitespace() {
        String rawIds = " 123 , 456 , 789 ";
        List<Long> parsed = idsAsCsvParser.parseRawIdsString(rawIds);
        String converted = idsAsCsvParser.toRawIdsString(parsed);

        assertThat(converted)
            .isEqualTo("123,456,789");
    }

    @Test
    void shouldParseWithCustomDelimiterWhenDelimiterIsConfigured() {
        when(csvStringProperties.delimiter()).thenReturn(";");
        idsAsCsvParser = new IdsAsCsvParser(csvStringProperties);

        String rawIds = "123;456;789";
        List<Long> result = idsAsCsvParser.parseRawIdsString(rawIds);

        assertThat(result)
            .hasSize(3)
            .containsExactly(123L, 456L, 789L);
    }

    @Test
    void shouldConvertWithCustomDelimiterWhenDelimiterIsConfigured() {
        when(csvStringProperties.delimiter()).thenReturn(";");
        idsAsCsvParser = new IdsAsCsvParser(csvStringProperties);

        List<Long> ids = List.of(123L, 456L, 789L);
        String result = idsAsCsvParser.toRawIdsString(ids);

        assertThat(result)
            .isEqualTo("123;456;789");
    }
}