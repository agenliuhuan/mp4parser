package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;

import java.util.Map;

/**
 * Assigns samples to groups.
 */
public interface SampleGroupExtension<T extends GroupEntry> {
    Map<T, long[]> getGroupEntries();
}
