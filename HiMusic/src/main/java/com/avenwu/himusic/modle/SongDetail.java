package com.avenwu.himusic.modle;

import com.avenwu.himusic.utils.FormatterHelper;

/**
 * @author chaobin
 * @date 11/22/13.
 */
public class SongDetail {
    public String title;
    public String artist;
    public long size;
    public long duration;

    @Override
    public String toString() {
        return new StringBuilder()
                .append(super.toString())
                .append("\ntitle:").append(title)
                .append("\nartist:").append(artist)
                .append("\nsize:").append(FormatterHelper.getReadableSize(size))
                .append("\nduration:").append(FormatterHelper.getReadleDuration(duration))
                .toString();
    }
}
