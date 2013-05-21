package com.alibaba.doris.dataserver.event;

import com.alibaba.doris.dataserver.DataServerException;

/**
 * @author ajun Email:jack.yuj@alibaba-inc.com
 */
public class EventListnerException extends DataServerException {

    private static final long serialVersionUID = -6890358775998163751L;

    /**
     * Creates a new exception.
     */
    public EventListnerException(Event event, EventListener listener) {
        super();
    }

    /**
     * Creates a new exception.
     */
    public EventListnerException(String message, Throwable cause, Event event, EventListener listener) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     */
    public EventListnerException(String message, Event event, EventListener listener) {
        super(message);
    }

    /**
     * Creates a new exception.
     */
    public EventListnerException(Throwable cause, Event event, EventListener listener) {
        super(cause);
    }

    public Event getEvent() {
        return event;
    }

    public EventListener getListener() {
        return listener;
    }

    private Event         event;
    private EventListener listener;
}
