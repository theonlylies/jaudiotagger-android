package org.jaudiotagger.issues;

import android.graphics.Bitmap;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.jaudiotagger.utils.BitmapUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test APIC Frame with no PictureType Field
 */
public class Issue224Test extends AbstractTestCase
{

    @Test
    public void testReadInvalidPicture()
    {
        String genre = null;

        File orig = new File("testdata", "test31.mp3");
        if (!orig.isFile())
        {
            return;
        }

        Exception exceptionCaught = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test31.mp3");
            AudioFile f = AudioFileIO.read(testFile);
            Tag tag = f.getTag();
            assertEquals(11, tag.getFieldCount());
            assertTrue(tag instanceof ID3v23Tag);
            ID3v23Tag id3v23Tag = (ID3v23Tag) tag;
            TagField coverArtField = id3v23Tag.getFirstField(org.jaudiotagger.tag.id3.ID3v23FieldKey.COVER_ART.getFieldName());
            assertTrue(coverArtField instanceof ID3v23Frame);
            assertTrue(((ID3v23Frame) coverArtField).getBody() instanceof FrameBodyAPIC);
            FrameBodyAPIC body = (FrameBodyAPIC) ((ID3v23Frame) coverArtField).getBody();
            byte[] imageRawData = body.getImageData();
            Bitmap bi = BitmapUtils.decodeByteArray(imageRawData);
            assertEquals(953, bi.getWidth());
            assertEquals(953, bi.getHeight());

            assertEquals("image/png", body.getMimeType());
            assertEquals("", body.getDescription());
            assertEquals("", body.getImageUrl());

            //This is an invalid value (probably first value of PictureType)
            assertEquals(3, body.getPictureType());

            assertFalse(body.isImageUrl());

            //SetDescription
            body.setDescription("FREDDY");
            assertEquals("FREDDY", body.getDescription());
            f.commit();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
        assertNull(genre);

    }
}