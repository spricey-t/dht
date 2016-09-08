package com.virohtus.dht.evt;

/**
 * Represents a generic system event. All events must be serializable
 * into byte[] and deserialized from byte[] - this allows for events to be transmitted
 * over the internet. This is meant to be extendable so custom events can be created and
 * handled
 *
 */
public class Event {

    public Event(byte[] data) {
    }



}
