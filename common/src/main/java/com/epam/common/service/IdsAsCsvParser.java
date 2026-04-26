package com.epam.common.service;

import java.util.Arrays;
import java.util.List;

import com.epam.common.config.property.CsvStringProperties;
import com.epam.common.exception.CsvIdsLengthExceededException;
import com.epam.common.exception.InvalidIdParseException;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class IdsAsCsvParser {

    private final CsvStringProperties csvStringProperties;

    public List<Long> parseRawIdsString(String rawIds) {
        if (StringUtils.isBlank(rawIds)) {
            throw new InvalidIdParseException(rawIds);
        }
        if (rawIds.length() > csvStringProperties.maxLength()) {
            throw new CsvIdsLengthExceededException(rawIds, csvStringProperties.maxLength());
        }

        return Arrays.stream(rawIds.split(csvStringProperties.delimiter()))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .map(this::parseSingleId)
            .toList();
    }

    private Long parseSingleId(String rawId) {
        try {
            return Long.valueOf(rawId.trim());
        } catch (NumberFormatException e) {
            throw new InvalidIdParseException(rawId, e);
        }
    }
}
