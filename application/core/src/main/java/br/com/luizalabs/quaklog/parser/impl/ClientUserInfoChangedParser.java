package br.com.luizalabs.quaklog.parser.impl;

import br.com.luizalabs.quaklog.parser.GameParserException;
import br.com.luizalabs.quaklog.parser.GameRegexUtils;
import br.com.luizalabs.quaklog.parser.Parsable;
import br.com.luizalabs.quaklog.parser.objects.ClientUserInfoChangedObParser;
import lombok.val;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static br.com.luizalabs.quaklog.parser.GameRegexUtils.SINGLE_ID_AFTER_KEY_PATTERN;
import static br.com.luizalabs.quaklog.parser.GameRegexUtils.extractInteger;

public class ClientUserInfoChangedParser implements Parsable<ClientUserInfoChangedObParser> {

    @Override
    public ClientUserInfoChangedObParser parse(String value) throws GameParserException {
        try {
            final val stringStringMap = extractParameters(value);
            return ClientUserInfoChangedObParser.builder()
                    .gameTime(extractTime(value))
                    .id(extractUserID(value))
                    .arguments(stringStringMap)
                    .name(Objects.requireNonNull(stringStringMap).get("n"))
                    .build();
        } catch (Exception e) {
            throw new GameParserException(e.getMessage(), e);
        }
    }

    private Integer extractUserID(String value) {
        return extractInteger(SINGLE_ID_AFTER_KEY_PATTERN, value, -1);
    }

    private Map<String, String> extractParameters(String value) {
        //ISSUE: Poderia pensar em um regex melhor
        final val matcher = GameRegexUtils.AFTER_KEY.matcher(value);
        if (matcher.find()) {
            final val afterKey = matcher.group();
            val matcher2 = GameRegexUtils.AFTER_NUMERIC.matcher(afterKey);
            String parameters;
            if (matcher2.find()) {
                parameters = matcher2.group();
            } else {
                parameters = afterKey;
            }
            return GameRegexUtils.extractPairsMap("\\", parameters);
        }
        return Collections.emptyMap();
    }


}
