package com.cellwize.hson.parsers;

import com.cellwize.hson.eventbroker.api.EventPublisher;

import java.io.InputStream;
import java.net.URI;

/**
 * Created by max on 18/11/2014.
 */
public interface Parser {
    public void setResultHandler(EventPublisher resultHandler);
    public void parse(URI uri, InputStream inputStream) throws ParserException;
}
